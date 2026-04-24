// State
let currentView = 'dashboard';
let autoRefreshInterval = null;

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    // Menu navigation
    document.querySelectorAll('.menu-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const view = e.currentTarget.getAttribute('data-view');
            switchView(view);
        });
    });

    // Initial loads
    fetchStatus();
    loadDashboard();

    // Set up polling
    setInterval(fetchStatus, 15000); // 15s polling for orchestrator
});

// Navigation
function switchView(viewName) {
    // Update menu UI
    document.querySelectorAll('.menu-item').forEach(item => item.classList.remove('active'));
    document.querySelector(`[data-view="${viewName}"]`).classList.add('active');

    // Hide all views
    document.querySelectorAll('.view').forEach(view => view.style.display = 'none');
    
    // Show target view
    document.getElementById(`view-${viewName}`).style.display = 'block';
    
    // Clear any previous view-specific polling
    if(autoRefreshInterval) clearInterval(autoRefreshInterval);

    // Load data based on view
    if (viewName === 'dashboard') {
        loadDashboard();
        autoRefreshInterval = setInterval(loadDashboard, 15000);
    } else if (viewName === 'tasks') {
        loadTasks();
    } else if (viewName === 'executions') {
        loadExecutions();
        autoRefreshInterval = setInterval(loadExecutions, 10000);
    }
}

// API Calls
async function fetchStatus() {
    try {
        const res = await fetch('/api/orchestrator/status');
        if (!res.ok) throw new Error('API Offline');
        const data = await res.json();
        
        document.getElementById('running-tasks-count').textContent = data.runningTasks || 0;
        document.getElementById('active-threads-count').textContent = data.activeThreads || 0;
        
        const ind = document.querySelector('.status-indicator .dot');
        ind.classList.add('active');
        document.getElementById('orchestrator-state').textContent = 'Online';
    } catch (e) {
        const ind = document.querySelector('.status-indicator .dot');
        ind.classList.remove('active');
        document.getElementById('orchestrator-state').textContent = 'Offline';
    }
}

async function loadDashboard() {
    try {
        // Fetch tasks stats
        const tasksRes = await fetch('/api/tasks?size=1000');
        const tasksData = await tasksRes.json();
        document.getElementById('total-tasks-stat').textContent = tasksData.totalElements;

        // Fetch execution stats
        const execRes = await fetch('/api/executions?size=10');
        const execData = await execRes.json();
        
        let completed = 0, failed = 0;
        // Approximation from page, for real we'd use aggregate endpoints
        execData.content.forEach(ex => {
            if(ex.status === 'COMPLETED') completed++;
            if(ex.status === 'FAILED') failed++;
        });
        document.getElementById('completed-exec-stat').textContent = completed;
        document.getElementById('failed-exec-stat').textContent = failed;

        // Render recent table
        renderExecutionsTable(execData.content, 'recent-executions-table');
        
        const emptyState = document.getElementById('recent-exec-empty');
        const table = document.getElementById('recent-executions-table');
        if (execData.content.length === 0) {
            emptyState.style.display = 'flex';
            table.style.display = 'none';
        } else {
            emptyState.style.display = 'none';
            table.style.display = 'table';
        }
    } catch (e) {
        console.error('Error loading dashboard', e);
    }
}

async function loadTasks() {
    try {
        const res = await fetch('/api/tasks?size=100');
        const data = await res.json();
        const tbody = document.querySelector('#tasks-table tbody');
        tbody.innerHTML = '';
        
        data.content.forEach(task => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>#${task.id}</td>
                <td><strong>${task.name}</strong></td>
                <td><span class="badge info">${task.taskType}</span></td>
                <td>${task.cronExpression || 'One-shot'}</td>
                <td><span class="badge ${task.enabled ? 'success' : 'danger'}">${task.enabled ? 'Active' : 'Disabled'}</span></td>
                <td class="actions-cell">
                    <button class="btn btn-sm btn-primary" onclick="runTask(${task.id})" title="Run Now"><i class="fa-solid fa-play"></i></button>
                    <button class="btn btn-sm btn-danger" onclick="deleteTask(${task.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch(e) {
        showToast('Error loading tasks', 'error');
    }
}

async function loadExecutions() {
    try {
        const res = await fetch('/api/executions?size=50');
        const data = await res.json();
        renderExecutionsTable(data.content, 'executions-table', true);
    } catch(e) {
        console.error(e);
    }
}

function renderExecutionsTable(executions, tableId, full = false) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';
    executions.forEach(ex => {
        const statusClass = {
            'COMPLETED': 'success',
            'FAILED': 'danger',
            'RUNNING': 'info',
            'PENDING': 'warning',
            'TIMED_OUT': 'danger',
            'CANCELLED': 'warning'
        }[ex.status] || 'info';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>#${ex.id}</td>
            <td>Task #${ex.taskDefinitionId}</td>
            <td><span class="badge ${statusClass}">${ex.status}</span></td>
            ${full ? `<td>${new Date(ex.startTime || ex.createdAt).toLocaleString()}</td>` : `<td>${calculateDuration(ex.startTime, ex.endTime)}</td>`}
            ${full ? `<td>${ex.endTime ? new Date(ex.endTime).toLocaleString() : '-'}</td>` : `<td>${new Date(ex.startTime || ex.createdAt).toLocaleTimeString()}</td>`}
            <td class="actions-cell">
                <button class="btn btn-sm btn-secondary" onclick="viewOutput(${ex.id})" title="View Output"><i class="fa-solid fa-terminal"></i></button>
                ${ex.status === 'RUNNING' || ex.status === 'PENDING' ? 
                  `<button class="btn btn-sm btn-warning" onclick="cancelExecution(${ex.id})" title="Cancel"><i class="fa-solid fa-stop"></i></button>` : ''}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Actions
async function runTask(id) {
    try {
        const res = await fetch(`/api/tasks/${id}/run`, { method: 'POST' });
        if(res.ok) {
            showToast('Task triggered successfully', 'success');
            if(currentView === 'executions') loadExecutions();
            else switchView('executions');
        } else {
            showToast('Failed to trigger task', 'error');
        }
    } catch(e) {
        showToast('Network error', 'error');
    }
}

async function cancelExecution(id) {
    if(!confirm('Cancel execution?')) return;
    try {
        const res = await fetch(`/api/executions/${id}/cancel`, { method: 'POST' });
        if(res.ok) {
            showToast('Cancel signal sent', 'success');
            loadExecutions();
        } else {
            showToast('Failed to cancel', 'error');
        }
    } catch(e) {
        showToast('Network error', 'error');
    }
}

async function deleteTask(id) {
    if(!confirm('Are you sure you want to delete this task?')) return;
    try {
        const res = await fetch(`/api/tasks/${id}`, { method: 'DELETE' });
        if(res.ok) {
            showToast('Task deleted', 'success');
            loadTasks();
        } else {
            showToast('Failed to delete task', 'error');
        }
    } catch(e) {
        showToast('Network error', 'error');
    }
}

async function viewOutput(id) {
    document.getElementById('execution-output').textContent = 'Loading...';
    document.getElementById('output-status').className = 'badge';
    document.getElementById('output-status').textContent = '...';
    openModal('output-modal');

    try {
        const res = await fetch(`/api/executions/${id}`);
        const data = await res.json();
        
        const output = data.output || data.errorMessage || 'No output recorded.';
        document.getElementById('execution-output').textContent = output;
        
        const statusClass = {
            'COMPLETED': 'success', 'FAILED': 'danger', 'RUNNING': 'info', 
            'PENDING': 'warning', 'TIMED_OUT': 'danger', 'CANCELLED': 'warning'
        }[data.status] || 'info';
        
        const statusBadge = document.getElementById('output-status');
        statusBadge.textContent = data.status;
        statusBadge.className = `badge ${statusClass}`;
    } catch(e) {
        document.getElementById('execution-output').textContent = 'Failed to load output.';
    }
}

// Modals
function openCreateModal() {
    document.getElementById('create-task-form').reset();
    openModal('create-modal');
}

function openModal(id) {
    document.getElementById(id).classList.add('active');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

async function submitCreateTask() {
    const payload = {
        name: document.getElementById('task-name').value,
        description: document.getElementById('task-desc').value,
        taskType: document.getElementById('task-type').value,
        config: document.getElementById('task-config').value,
        cronExpression: document.getElementById('task-cron').value || null,
        maxRetries: parseInt(document.getElementById('task-retries').value),
        retryDelaySeconds: parseInt(document.getElementById('task-delay').value),
        timeoutSeconds: parseInt(document.getElementById('task-timeout').value),
        enabled: document.getElementById('task-enabled').checked,
        userId: parseInt(document.getElementById('task-userid').value)
    };

    try {
        // Validate JSON
        JSON.parse(payload.config);
    } catch(e) {
        showToast('Invalid Config JSON', 'error');
        return;
    }

    if(!payload.name) {
        showToast('Name is required', 'error');
        return;
    }

    try {
        const res = await fetch('/api/tasks', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if(res.ok) {
            showToast('Task created successfully', 'success');
            closeModal('create-modal');
            if(currentView === 'tasks') loadTasks();
            else switchView('tasks');
        } else {
            const err = await res.text();
            showToast('Failed: ' + err, 'error');
        }
    } catch(e) {
        showToast('Network error', 'error');
    }
}

// Utils
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const iconClass = type === 'success' ? 'fa-check' : 'fa-exclamation';
    toast.innerHTML = `<div class="toast-icon"><i class="fa-solid ${iconClass}"></i></div> <span>${message}</span>`;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('fadeOut');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function calculateDuration(start, end) {
    if (!start) return '-';
    const s = new Date(start).getTime();
    const e = end ? new Date(end).getTime() : Date.now();
    const diff = (e - s) / 1000; // seconds
    if (diff < 60) return `${Math.floor(diff)}s`;
    const m = Math.floor(diff / 60);
    const rs = Math.floor(diff % 60);
    return `${m}m ${rs}s`;
}

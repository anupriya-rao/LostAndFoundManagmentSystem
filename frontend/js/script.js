// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Login Form Handler
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const role = document.getElementById('role').value;
            
            loginUser(email, password, role);
        });
    }
});

// Login User
function loginUser(email, password, role) {
    const loginData = {
        email: email,
        password: password,
        role: role
    };
    
    fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            localStorage.setItem('authToken', data.token);
            localStorage.setItem('userRole', role);
            localStorage.setItem('userId', data.userId);
            
            if (role === 'ADMIN') {
                window.location.href = 'pages/admin-dashboard.html';
            } else {
                window.location.href = 'pages/dashboard.html';
            }
        } else {
            showAlert('Login failed: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('An error occurred. Please try again.', 'error');
    });
}

// Get Items
function getItems(endpoint) {
    const token = localStorage.getItem('authToken');
    
    return fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .catch(error => {
        console.error('Error:', error);
        showAlert('Failed to fetch data', 'error');
    });
}

// Create Item
function createItem(endpoint, itemData) {
    const token = localStorage.getItem('authToken');
    
    return fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(itemData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('Item created successfully', 'success');
            return data;
        } else {
            showAlert('Failed to create item: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('An error occurred', 'error');
    });
}

// Update Item
function updateItem(endpoint, itemId, itemData) {
    const token = localStorage.getItem('authToken');
    
    return fetch(`${API_BASE_URL}${endpoint}/${itemId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(itemData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('Item updated successfully', 'success');
            return data;
        } else {
            showAlert('Failed to update item: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('An error occurred', 'error');
    });
}

// Delete Item
function deleteItem(endpoint, itemId) {
    const token = localStorage.getItem('authToken');
    
    return fetch(`${API_BASE_URL}${endpoint}/${itemId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('Item deleted successfully', 'success');
            return data;
        } else {
            showAlert('Failed to delete item: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('An error occurred', 'error');
    });
}

// Logout
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    window.location.href = '../index.html';
}

// Show Alert
function showAlert(message, type = 'info') {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(alert, container.firstChild);
    
    setTimeout(() => {
        alert.remove();
    }, 3000);
}

// Check Authentication
function checkAuth() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = '../index.html';
    }
}

// Format Date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Render Items Table
function renderItemsTable(items, containerId) {
    const container = document.getElementById(containerId);
    
    if (!items || items.length === 0) {
        container.innerHTML = '<p>No items found</p>';
        return;
    }
    
    let html = '<table><thead><tr><th>ID</th><th>Description</th><th>Location</th><th>Date</th><th>Status</th><th>Actions</th></tr></thead><tbody>';
    
    items.forEach(item => {
        html += `
            <tr>
                <td>${item.id}</td>
                <td>${item.description}</td>
                <td>${item.location}</td>
                <td>${formatDate(item.dateReported)}</td>
                <td><span class="badge badge-${item.status.toLowerCase()}">${item.status}</span></td>
                <td>
                    <button class="btn btn-secondary" onclick="viewItem(${item.id})">View</button>
                </td>
            </tr>
        `;
    });
    
    html += '</tbody></table>';
    container.innerHTML = html;
}

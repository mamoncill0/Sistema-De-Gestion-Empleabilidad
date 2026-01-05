const { useState, useEffect } = React;

// API Configuration
const API_BASE_URL = 'http://localhost:8080';

// --- Helper function for creating authenticated headers ---
const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
};

// API Service
const api = {
    // Auth
    login: async (credentials) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
        if (!response.ok) throw new Error('Login failed');
        return response.json();
    },

    register: async (userData) => {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        if (!response.ok) throw new Error('Registration failed');
        return response.text();
    },

    // Users
    getUsers: async () => {
        const response = await fetch(`${API_BASE_URL}/api/users`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to fetch users');
        return response.json();
    },

    // Projects
    getProjects: async () => {
        const response = await fetch(`${API_BASE_URL}/api/projects`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to fetch projects');
        return response.json();
    },

    createProject: async (projectData) => {
        const response = await fetch(`${API_BASE_URL}/api/projects`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(projectData)
        });
        if (!response.ok) throw new Error('Failed to create project');
        return response.json();
    },

    activateProject: async (projectId) => {
        const response = await fetch(`${API_BASE_URL}/api/projects/${projectId}/activate`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to activate project');
        }
        return response.json();
    },

    deleteProject: async (projectId) => {
        const response = await fetch(`${API_BASE_URL}/api/projects/${projectId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to delete project');
        return response.json();
    },

    // Tasks
    createTask: async (projectId, taskData) => {
        const response = await fetch(`${API_BASE_URL}/api/tasks/projects/${projectId}`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(taskData)
        });
        if (!response.ok) throw new Error('Failed to create task');
        return response.json();
    },

    completeTask: async (taskId) => {
        const response = await fetch(`${API_BASE_URL}/api/tasks/${taskId}/complete`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to complete task');
        return response.json();
    },

    deleteTask: async (taskId) => {
        const response = await fetch(`${API_BASE_URL}/api/tasks/${taskId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to delete task');
        return response.json();
    }
};

// Auth Component
function Auth({ onLogin }) {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        role: 'USER'
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            if (isLogin) {
                const data = await api.login({
                    username: formData.username,
                    password: formData.password
                });
                localStorage.setItem('token', data.accessToken);
                localStorage.setItem('user', JSON.stringify({
                    id: data.id,
                    username: data.username,
                    email: data.email,
                    role: data.role
                }));
                onLogin();
            } else {
                await api.register({
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                    role: [formData.role]
                });
                setSuccess('Usuario registrado exitosamente. Por favor inicia sesión.');
                setIsLogin(true);
                setFormData({ username: '', email: '', password: '', role: 'USER' });
            }
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="auth-container">
            <h2>{isLogin ? 'Iniciar Sesión' : 'Registrarse'}</h2>
            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Usuario</label>
                    <input
                        type="text"
                        value={formData.username}
                        onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                        required
                    />
                </div>
                {!isLogin && (
                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            required
                        />
                    </div>
                )}
                <div className="form-group">
                    <label>Contraseña</label>
                    <input
                        type="password"
                        value={formData.password}
                        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        required
                    />
                </div>
                {!isLogin && (
                    <div className="form-group">
                        <label>Rol</label>
                        <select
                            value={formData.role}
                            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                        >
                            <option value="USER">User</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>
                )}
                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                    {isLogin ? 'Entrar' : 'Registrarse'}
                </button>
            </form>
            <div className="auth-toggle">
                {isLogin ? '¿No tienes cuenta? ' : '¿Ya tienes cuenta? '}
                <a onClick={() => setIsLogin(!isLogin)}>
                    {isLogin ? 'Regístrate' : 'Inicia sesión'}
                </a>
            </div>
        </div>
    );
}

// Admin Dashboard Component
function AdminDashboard({ projects, users, onLogout }) {
    return (
        <div className="container">
            <div className="header">
                <h1>📋 Admin Dashboard</h1>
                <button className="btn btn-secondary" onClick={onLogout}>
                    Cerrar Sesión
                </button>
            </div>
            <div className="admin-section">
                <h2>All Users ({users.length})</h2>
                <div className="user-list">
                    {users.map(user => (
                        <div key={user.idUser} className="user-item">
                            <span>{user.username} ({user.email}) - Role: {user.role}</span>
                        </div>
                    ))}
                </div>
            </div>
            <div className="admin-section">
                <h2>All Projects ({projects.length})</h2>
                <div className="project-list">
                    {projects.map(project => (
                        <div key={project.idProject} className="project-item">
                            <span>{project.name} - Status: {project.status}</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}


// Modal Component
function Modal({ title, onClose, children }) {
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <h3>{title}</h3>
                {children}
            </div>
        </div>
    );
}

// Project Card Component
function ProjectCard({ project, tasks, onActivate, onDelete, onAddTask, onCompleteTask, onDeleteTask }) {
    const [showTaskModal, setShowTaskModal] = useState(false);
    const [taskTitle, setTaskTitle] = useState('');
    const [loading, setLoading] = useState(false);

    const projectTasks = tasks.filter(t => t.projectId === project.idProject && !t.deleted);

    const handleAddTask = async () => {
        if (!taskTitle.trim()) return;
        setLoading(true);
        try {
            await onAddTask(project.idProject, taskTitle);
            setTaskTitle('');
            setShowTaskModal(false);
        } catch (err) {
            alert('Error al crear tarea: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="project-card">
            <div className="project-header">
                <h3 className="project-title">{project.name}</h3>
                <span className={`project-status status-${project.status.toLowerCase()}`}>
                    {project.status}
                </span>
            </div>

            <div className="tasks-section">
                <div className="tasks-header">
                    <h4>Tareas ({projectTasks.length})</h4>
                    <button className="btn btn-primary btn-small" onClick={() => setShowTaskModal(true)}>
                        + Agregar Tarea
                    </button>
                </div>

                {projectTasks.length === 0 ? (
                    <div className="empty-state">
                        <p>No hay tareas en este proyecto</p>
                    </div>
                ) : (
                    projectTasks.map(task => (
                        <div key={task.idTask} className="task-item">
                            <div className="task-info">
                                <input
                                    type="checkbox"
                                    className="task-checkbox"
                                    checked={task.completed}
                                    onChange={() => !task.completed && onCompleteTask(task.idTask)}
                                    disabled={task.completed}
                                />
                                <span className={`task-title ${task.completed ? 'completed' : ''}`}>
                                    {task.title}
                                </span>
                            </div>
                            <div className="task-actions">
                                {!task.completed && (
                                    <button
                                        className="btn btn-danger btn-small"
                                        onClick={() => onDeleteTask(task.idTask)}
                                    >
                                        Eliminar
                                    </button>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>

            <div className="project-actions">
                {project.status === 'DRAFT' && (
                    <button
                        className="btn btn-success btn-small"
                        onClick={() => onActivate(project.idProject)}
                    >
                        Activar Proyecto
                    </button>
                )}
                <button
                    className="btn btn-danger btn-small"
                    onClick={() => onDelete(project.idProject)}
                >
                    Eliminar Proyecto
                </button>
            </div>

            {showTaskModal && (
                <Modal title="Nueva Tarea" onClose={() => setShowTaskModal(false)}>
                    <div className="form-group">
                        <label>Título de la tarea</label>
                        <input
                            type="text"
                            value={taskTitle}
                            onChange={(e) => setTaskTitle(e.target.value)}
                            placeholder="Ingresa el título"
                        />
                    </div>
                    <div className="modal-actions">
                        <button
                            className="btn btn-secondary"
                            onClick={() => setShowTaskModal(false)}
                        >
                            Cancelar
                        </button>
                        <button
                            className="btn btn-primary"
                            onClick={handleAddTask}
                            disabled={loading}
                        >
                            {loading ? 'Creando...' : 'Crear Tarea'}
                        </button>
                    </div>
                </Modal>
            )}
        </div>
    );
}

// Main App Component
function Bundle() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [projects, setProjects] = useState([]);
    const [users, setUsers] = useState([]);
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showProjectModal, setShowProjectModal] = useState(false);
    const [projectName, setProjectName] = useState('');

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
            const parsedUser = JSON.parse(storedUser);
            setUser(parsedUser);
            setIsAuthenticated(true);
        } else {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (isAuthenticated && user) {
            if (user.role === 'ADMIN') {
                loadAdminData();
            } else {
                loadProjects();
            }
        }
    }, [isAuthenticated, user]);

    const loadProjects = async () => {
        setLoading(true);
        try {
            const projectsData = await api.getProjects();
            setProjects(projectsData);
        } catch (err) {
            console.error('Error loading projects:', err);
            if (err.message.includes('401') || err.message.includes('Failed to fetch')) {
                handleLogout();
            }
        } finally {
            setLoading(false);
        }
    };

    const loadAdminData = async () => {
        setLoading(true);
        try {
            const [projectsData, usersData] = await Promise.all([
                api.getProjects(),
                api.getUsers()
            ]);
            setProjects(projectsData);
            setUsers(usersData);
        } catch (err) {
            console.error('Error loading admin data:', err);
            if (err.message.includes('401') || err.message.includes('Failed to fetch')) {
                handleLogout();
            }
        } finally {
            setLoading(false);
        }
    };

    const handleLogin = () => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            const parsedUser = JSON.parse(storedUser);
            setUser(parsedUser);
            setIsAuthenticated(true);
            // After setting user, useEffect will trigger data loading
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
        setIsAuthenticated(false);
        setProjects([]);
        setUsers([]);
        setTasks([]);
    };

    const handleCreateProject = async () => {
        if (!projectName.trim()) return;
        setLoading(true);
        try {
            await api.createProject({ name: projectName });
            setProjectName('');
            setShowProjectModal(false);
            await loadProjects();
        } catch (err) {
            alert('Error al crear proyecto: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleActivateProject = async (projectId) => {
        try {
            await api.activateProject(projectId);
            await loadProjects();
        } catch (err) {
            alert('Error: ' + err.message);
        }
    };

    const handleDeleteProject = async (projectId) => {
        if (!confirm('¿Estás seguro de eliminar este proyecto?')) return;
        try {
            await api.deleteProject(projectId);
            await loadProjects();
        } catch (err) {
            alert('Error al eliminar proyecto: ' + err.message);
        }
    };

    const handleAddTask = async (projectId, title) => {
        const newTask = await api.createTask(projectId, { title });
        setTasks([...tasks, newTask]);
        return newTask;
    };

    const handleCompleteTask = async (taskId) => {
        try {
            const updatedTask = await api.completeTask(taskId);
            setTasks(tasks.map(t => t.idTask === taskId ? updatedTask : t));
        } catch (err) {
            alert('Error al completar tarea: ' + err.message);
        }
    };

    const handleDeleteTask = async (taskId) => {
        if (!confirm('¿Estás seguro de eliminar esta tarea?')) return;
        try {
            await api.deleteTask(taskId);
            setTasks(tasks.filter(t => t.idTask !== taskId));
        } catch (err) {
            alert('Error al eliminar tarea: ' + err.message);
        }
    };

    if (!isAuthenticated) {
        return <Auth onLogin={handleLogin} />;
    }

    if (loading) {
        return (
            <div className="loading">
                <div className="spinner"></div>
            </div>
        );
    }

    if (user && user.role === 'ADMIN') {
        return <AdminDashboard projects={projects} users={users} onLogout={handleLogout} />;
    }

    return (
        <div className="container">
            <div className="header">
                <h1>📋 Project Manager</h1>
                <div className="user-info">
                    <span>👤 {user?.username}</span>
                    <button className="btn btn-secondary" onClick={handleLogout}>
                        Cerrar Sesión
                    </button>
                </div>
            </div>

            <div className="projects-section">
                <div className="section-header">
                    <h2>Mis Proyectos</h2>
                    <button
                        className="btn btn-primary"
                        onClick={() => setShowProjectModal(true)}
                    >
                        + Nuevo Proyecto
                    </button>
                </div>

                {projects.length === 0 ? (
                    <div className="empty-state">
                        <h3>No tienes proyectos aún</h3>
                        <p>Crea tu primer proyecto para comenzar</p>
                    </div>
                ) : (
                    projects
                        .filter(p => !p.deleted)
                        .map(project => (
                            <ProjectCard
                                key={project.idProject}
                                project={project}
                                tasks={tasks}
                                onActivate={handleActivateProject}
                                onDelete={handleDeleteProject}
                                onAddTask={handleAddTask}
                                onCompleteTask={handleCompleteTask}
                                onDeleteTask={handleDeleteTask}
                            />
                        ))
                )}
            </div>

            {showProjectModal && (
                <Modal title="Nuevo Proyecto" onClose={() => setShowProjectModal(false)}>
                    <div className="form-group">
                        <label>Nombre del proyecto</label>
                        <input
                            type="text"
                            value={projectName}
                            onChange={(e) => setProjectName(e.target.value)}
                            placeholder="Ingresa el nombre"
                        />
                    </div>
                    <div className="modal-actions">
                        <button
                            className="btn btn-secondary"
                            onClick={() => setShowProjectModal(false)}
                        >
                            Cancelar
                        </button>
                        <button
                            className="btn btn-primary"
                            onClick={handleCreateProject}
                            disabled={loading}
                        >
                            {loading ? 'Creando...' : 'Crear Proyecto'}
                        </button>
                    </div>
                </Modal>
            )}
        </div>
    );
}

// Render App
ReactDOM.render(<Bundle />, document.getElementById('root'));
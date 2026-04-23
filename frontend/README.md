# Lost and Found Management System - Frontend

A responsive web frontend for the Lost and Found Management System built with HTML, CSS, and JavaScript.

## Project Structure

```
frontend/
├── index.html              # Login/Landing page
├── css/
│   └── style.css          # Main stylesheet with responsive design
├── js/
│   └── script.js          # API integration and utility functions
└── pages/
    ├── signup.html        # User registration page
    ├── dashboard.html     # User dashboard
    ├── lost-items.html    # Browse lost items
    ├── found-items.html   # Browse found items
    ├── report-lost.html   # Report a lost item
    ├── report-found.html  # Report a found item
    ├── claims.html        # Manage claims
    ├── admin-dashboard.html   # Admin overview
    ├── admin-users.html   # Manage users (admin)
    ├── admin-items.html   # Manage all items (admin)
    └── admin-claims.html  # Manage claims (admin)
```

## Features

### User Features
- **Authentication**: Login and signup with email/password
- **Report Items**: Report lost or found items with details
- **Browse Items**: Search and view lost and found items
- **Claims Management**: Submit and track claim requests
- **Item Management**: View and manage your reported items

### Admin Features
- **Dashboard**: Overview of system statistics
- **User Management**: Manage registered users
- **Item Management**: View and manage all items
- **Claims Management**: Review and process claims

## API Integration

The frontend communicates with the backend API at `http://localhost:8080/api`.

### Key API Endpoints

```
POST   /api/auth/login           - User login
POST   /api/auth/signup          - User registration
GET    /api/items/lost           - Get lost items
GET    /api/items/found          - Get found items
POST   /api/items/lost           - Report lost item
POST   /api/items/found          - Report found item
GET    /api/claims/my-claims     - Get user's claims
POST   /api/claims               - Submit claim
GET    /api/admin/stats          - Get statistics (admin)
GET    /api/admin/items/recent   - Get recent items (admin)
GET    /api/admin/claims/recent  - Get recent claims (admin)
```

## Setup Instructions

1. Ensure the backend API is running on `http://localhost:8080`
2. Open `index.html` in a web browser
3. Create an account or login with existing credentials
4. Navigate through the application

## Browser Compatibility

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Features

### Responsive Design
- Mobile-friendly layout
- Adaptive grid system
- Touch-friendly buttons and inputs

### User Experience
- Clean and intuitive interface
- Real-time search functionality
- Status badges for item/claim status
- Alert notifications for user actions

### Security
- Authentication tokens stored in localStorage
- Authorization headers for API requests
- Protected pages (require authentication)

## Styling

The project uses custom CSS with:
- Modern color scheme (purple gradient)
- Card-based layout
- Responsive grid system
- Smooth transitions and hover effects
- Badge system for status indicators

## JavaScript Features

- Fetch API for HTTP requests
- DOM manipulation
- Event handling
- Client-side validation
- Local storage for authentication

## Notes

- The API base URL can be modified in `script.js` by changing `API_BASE_URL`
- Authentication tokens are stored in `localStorage`
- The application uses session-based authentication
- Admin pages are accessible only to users with ADMIN role

## Future Enhancements

- Image upload for items
- Email notifications
- Advanced filtering and sorting
- Item history timeline
- User profile management
- PDF export for reports
- Real-time notifications with WebSockets

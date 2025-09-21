# Hospital Equipment Rental System

A full-stack web application for renting hospital equipment built with Spring Boot (backend) and React TypeScript (frontend).

## Features

### Customer Features
- Browse available medical equipment by category
- Search equipment by name, manufacturer, or description
- Filter equipment by price range and category
- View detailed equipment information
- Book equipment rentals with date selection
- Automatic cost calculation based on rental period

### Admin Features
- Dashboard with rental statistics
- Manage all rentals (approve, activate, complete, cancel)
- View all equipment inventory
- View customer information
- Track overdue rentals

### Equipment Categories
- 🦽 Mobility Equipment (wheelchairs, walkers, crutches, hospital beds)
- 📊 Monitoring Equipment (blood pressure monitors, pulse oximeters, thermometers)
- 💨 Respiratory Equipment (oxygen concentrators, nebulizers)
- 🏃‍♂️ Therapy Equipment (exercise bikes, rehabilitation equipment)

## Technology Stack

### Backend
- **Spring Boot 3.1.5** - Java framework
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database for development
- **Maven** - Dependency management
- **Spring Boot Validation** - Input validation

### Frontend
- **React 18** - Frontend framework
- **TypeScript** - Type safety
- **React Bootstrap** - UI components
- **React Router** - Navigation
- **Axios** - HTTP client

## Project Structure

```
hospital-equipment-rental/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/hospital/equipment/
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── service/        # Business logic layer
│   │   ├── controller/     # REST controllers
│   │   └── config/         # Configuration classes
│   └── pom.xml            # Maven dependencies
└── frontend/              # React application
    ├── src/
    │   ├── components/    # Reusable React components
    │   ├── pages/         # Page components
    │   ├── services/      # API service layer
    │   └── types/         # TypeScript type definitions
    └── package.json       # NPM dependencies
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- npm or yarn

### Running the Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

3. The backend will start on `http://localhost:8080`

4. Access the H2 database console at `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:equipmentdb`
   - Username: `sa`
   - Password: (leave empty)

### Running the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. The frontend will start on `http://localhost:3000`

## API Endpoints

### Equipment Endpoints
- `GET /api/equipment` - Get all equipment
- `GET /api/equipment/available` - Get available equipment
- `GET /api/equipment/{id}` - Get equipment by ID
- `GET /api/equipment/search?keyword={keyword}` - Search equipment
- `GET /api/equipment/category/{categoryId}` - Get equipment by category

### Customer Endpoints
- `GET /api/customers` - Get all customers
- `POST /api/customers` - Create new customer
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/email/{email}` - Get customer by email

### Rental Endpoints
- `GET /api/rentals` - Get all rentals
- `POST /api/rentals` - Create new rental
- `GET /api/rentals/{id}` - Get rental by ID
- `PATCH /api/rentals/{id}/status?status={status}` - Update rental status
- `GET /api/rentals/equipment/{equipmentId}/availability` - Check availability
- `GET /api/rentals/equipment/{equipmentId}/cost` - Calculate rental cost

### Category Endpoints
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create new category
- `GET /api/categories/{id}` - Get category by ID

## Sample Data

The application comes pre-loaded with sample data including:

### Categories
- Mobility Equipment
- Monitoring Equipment  
- Respiratory Equipment
- Therapy Equipment

### Equipment (10 items)
- Standard Wheelchair ($25/day)
- Folding Walker ($15/day)
- Electric Hospital Bed ($75/day)
- Digital Blood Pressure Monitor ($20/day)
- Pulse Oximeter ($10/day)
- Portable Oxygen Concentrator ($100/day)
- Compressor Nebulizer ($35/day)
- Stationary Exercise Bike ($45/day)
- Adjustable Crutches ($12/day)
- Digital Thermometer ($8/day)

### Sample Customers
- John Smith
- Sarah Johnson
- Michael Brown

## Usage

### For Customers

1. **Browse Equipment**: Visit the Equipment page to see all available medical equipment
2. **Search & Filter**: Use the search bar and filters to find specific equipment
3. **View Details**: Click on any equipment to see detailed information
4. **Book Rental**: Click "Rent Now" to start the booking process
5. **Fill Rental Form**: Provide your contact information and select rental dates
6. **Submit Request**: Review the estimated cost and submit your rental request

### For Administrators

1. **Access Admin Dashboard**: Go to `/admin` to access the admin interface
2. **View Statistics**: See overview of active rentals, pending approvals, and revenue
3. **Manage Rentals**: 
   - View all rental requests
   - Approve pending rentals (PENDING → CONFIRMED)
   - Activate confirmed rentals (CONFIRMED → ACTIVE)
   - Complete active rentals (ACTIVE → COMPLETED)
   - Cancel rentals if needed
4. **Monitor Equipment**: View equipment inventory and availability
5. **Customer Management**: View customer information and rental history

## Rental Status Flow

1. **PENDING** - Customer submits rental request
2. **CONFIRMED** - Admin approves the request
3. **ACTIVE** - Equipment is picked up/delivered
4. **COMPLETED** - Equipment is returned
5. **CANCELLED** - Rental is cancelled
6. **OVERDUE** - Rental has passed end date without return

## Development

### Adding New Equipment Categories

1. Add new category via the API or directly in `DataInitializer.java`
2. Update the frontend category filters if needed

### Adding New Equipment

1. Create equipment via admin interface (future feature) or add to `DataInitializer.java`
2. Include proper category assignment and pricing

### Customizing UI

1. Modify React components in `frontend/src/components/` and `frontend/src/pages/`
2. Update Bootstrap themes or add custom CSS as needed

## Troubleshooting

### Common Issues

1. **Port Conflicts**: 
   - Backend uses port 8080
   - Frontend uses port 3000
   - Make sure these ports are available

2. **CORS Issues**: 
   - The backend is configured to allow requests from `http://localhost:3000`
   - If using different ports, update the `@CrossOrigin` annotations

3. **Database Issues**:
   - H2 database is in-memory and resets on restart
   - Check H2 console for database state

### Error Messages

- **"Equipment not available"**: Check if equipment quantity is sufficient for the requested period
- **"Customer already exists"**: Email addresses must be unique
- **"Failed to fetch data"**: Ensure backend is running on port 8080

## Future Enhancements

- User authentication and authorization
- Payment processing integration
- Email notifications for rental confirmations
- Equipment maintenance scheduling
- Inventory management
- Reporting and analytics
- Mobile app support
- Multi-location support

## License

This project is for educational/demonstration purposes.
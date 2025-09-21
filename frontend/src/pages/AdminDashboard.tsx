import React, { useState, useEffect } from 'react';
import { 
  Row, Col, Card, Table, Button, Badge, Tab, Tabs, 
  Alert, Spinner, Modal 
} from 'react-bootstrap';
import { Equipment, Rental, Customer } from '../types';
import { equipmentAPI, rentalAPI, customerAPI } from '../services/api';

const AdminDashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState('rentals');
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [equipment, setEquipment] = useState<Equipment[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [selectedRental, setSelectedRental] = useState<Rental | null>(null);
  const [authenticated, setAuthenticated] = useState<boolean>(() => {
    return localStorage.getItem('adminAuthed') === 'true';
  });
  const [passwordInput, setPasswordInput] = useState('');
  const ADMIN_PASSWORD = 'Admin@123'; // hardcoded password

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [rentalsResponse, equipmentResponse, customersResponse] = await Promise.all([
        rentalAPI.getAll(),
        equipmentAPI.getAll(),
        customerAPI.getAll()
      ]);
      
      setRentals(rentalsResponse.data);
      setEquipment(equipmentResponse.data);
      setCustomers(customersResponse.data);
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (rentalId: number, newStatus: string) => {
    try {
      await rentalAPI.updateStatus(rentalId, newStatus);
      await fetchData(); // Refresh data
      setShowModal(false);
      setSelectedRental(null);
    } catch (err) {
      setError('Failed to update rental status');
      console.error('Error updating status:', err);
    }
  };

  const getStatusVariant = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'CONFIRMED':
        return 'info';
      case 'ACTIVE':
        return 'success';
      case 'COMPLETED':
        return 'secondary';
      case 'CANCELLED':
        return 'danger';
      case 'OVERDUE':
        return 'danger';
      default:
        return 'primary';
    }
  };

  const getEquipmentStatusVariant = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return 'success';
      case 'RENTED':
        return 'warning';
      case 'MAINTENANCE':
        return 'danger';
      case 'RETIRED':
        return 'secondary';
      default:
        return 'primary';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateTime = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <div className="text-center">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  if (error) {
    return <Alert variant="danger">{error}</Alert>;
  }

  if (!authenticated) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <div style={{ maxWidth: '400px', width: '100%' }}>
          <h2 className="mb-4 text-center">Admin Login</h2>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              value={passwordInput}
              onChange={(e) => setPasswordInput(e.target.value)}
              placeholder="Enter admin password"
            />
          </div>
          <div className="d-grid gap-2">
            <button
              className="btn btn-primary"
              onClick={() => {
                if (passwordInput === ADMIN_PASSWORD) {
                  localStorage.setItem('adminAuthed', 'true');
                  setAuthenticated(true);
                } else {
                  alert('Invalid password');
                }
              }}
              disabled={!passwordInput}
            >
              Login
            </button>
            {passwordInput && passwordInput !== ADMIN_PASSWORD && (
              <div className="text-danger small">Incorrect password</div>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Calculate statistics
  const activeRentals = rentals.filter(r => r.status === 'ACTIVE').length;
  const pendingRentals = rentals.filter(r => r.status === 'PENDING').length;
  const overdueRentals = rentals.filter(r => r.status === 'OVERDUE').length;
  const totalRevenue = rentals
    .filter(r => r.status === 'COMPLETED')
    .reduce((sum, r) => sum + r.totalAmount, 0);

  return (
    <div>
      <h1>Admin Dashboard</h1>
      
      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col md={3}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>{activeRentals}</Card.Title>
              <Card.Text>Active Rentals</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>{pendingRentals}</Card.Title>
              <Card.Text>Pending Approvals</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title className="text-danger">{overdueRentals}</Card.Title>
              <Card.Text>Overdue Rentals</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title className="text-success">${totalRevenue.toFixed(2)}</Card.Title>
              <Card.Text>Total Revenue</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Tabs */}
      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k || 'rentals')} className="mb-3">
        <Tab eventKey="rentals" title="Rentals">
          <Card>
            <Card.Body>
              <Table responsive striped hover>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Customer</th>
                    <th>Equipment</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Status</th>
                    <th>Total</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {rentals.map(rental => (
                    <tr key={rental.id}>
                      <td>{rental.id}</td>
                      <td>{rental.customer?.firstName} {rental.customer?.lastName}</td>
                      <td>{rental.equipment?.name}</td>
                      <td>{formatDate(rental.startDate)}</td>
                      <td>{formatDate(rental.endDate)}</td>
                      <td>
                        <Badge bg={getStatusVariant(rental.status)}>
                          {rental.status}
                        </Badge>
                      </td>
                      <td>${rental.totalAmount.toFixed(2)}</td>
                      <td>
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => {
                            setSelectedRental(rental);
                            setShowModal(true);
                          }}
                        >
                          Manage
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>

        <Tab eventKey="equipment" title="Equipment">
          <Card>
            <Card.Body>
              <Table responsive striped hover>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Manufacturer</th>
                    <th>Daily Price</th>
                    <th>Available/Total</th>
                    <th>Status</th>
                    <th>Image</th>
                    <th>Upload</th>
                  </tr>
                </thead>
                <tbody>
                  {equipment.map(item => (
                    <tr key={item.id}>
                      <td>{item.id}</td>
                      <td>{item.name}</td>
                      <td>{item.manufacturer}</td>
                      <td>${item.dailyPrice}</td>
                      <td>{item.availableQuantity}/{item.totalQuantity}</td>
                      <td>
                        <Badge bg={getEquipmentStatusVariant(item.status)}>
                          {item.status}
                        </Badge>
                      </td>
                      <td>
                        {item.imageUrl ? (
                          <img src={item.imageUrl} alt={item.name} style={{ width: '60px', height: '40px', objectFit: 'cover' }} />
                        ) : (
                          <span className="text-muted small">No image</span>
                        )}
                      </td>
                      <td>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={async (e) => {
                            const file = e.target.files?.[0];
                            if (!file) return;
                            try {
                              await equipmentAPI.uploadImage(item.id, file);
                              await fetchData();
                            } catch (err) {
                              alert('Failed to upload image');
                              console.error(err);
                            } finally {
                              e.target.value='';
                            }
                          }}
                          style={{ maxWidth: '140px' }}
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>

        <Tab eventKey="customers" title="Customers">
          <Card>
            <Card.Body>
              <Table responsive striped hover>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>City</th>
                    <th>State</th>
                  </tr>
                </thead>
                <tbody>
                  {customers.map(customer => (
                    <tr key={customer.id}>
                      <td>{customer.id}</td>
                      <td>{customer.firstName} {customer.lastName}</td>
                      <td>{customer.email}</td>
                      <td>{customer.phone}</td>
                      <td>{customer.city}</td>
                      <td>{customer.state}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>
      </Tabs>

      {/* Rental Management Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Manage Rental #{selectedRental?.id}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedRental && (
            <div>
              <Row>
                <Col md={6}>
                  <h5>Customer Information</h5>
                  <p><strong>Name:</strong> {selectedRental.customer?.firstName} {selectedRental.customer?.lastName}</p>
                  <p><strong>Email:</strong> {selectedRental.customer?.email}</p>
                  <p><strong>Phone:</strong> {selectedRental.customer?.phone}</p>
                </Col>
                <Col md={6}>
                  <h5>Equipment Information</h5>
                  <p><strong>Equipment:</strong> {selectedRental.equipment?.name}</p>
                  <p><strong>Quantity:</strong> {selectedRental.quantity}</p>
                  <p><strong>Daily Rate:</strong> ${selectedRental.dailyRate}</p>
                </Col>
              </Row>
              
              <hr />
              
              <Row>
                <Col md={6}>
                  <h5>Rental Period</h5>
                  <p><strong>Start Date:</strong> {formatDate(selectedRental.startDate)}</p>
                  <p><strong>End Date:</strong> {formatDate(selectedRental.endDate)}</p>
                  {selectedRental.actualReturnDate && (
                    <p><strong>Actual Return:</strong> {formatDate(selectedRental.actualReturnDate)}</p>
                  )}
                </Col>
                <Col md={6}>
                  <h5>Financial Information</h5>
                  <p><strong>Total Amount:</strong> ${selectedRental.totalAmount.toFixed(2)}</p>
                  <p><strong>Created:</strong> {formatDateTime(selectedRental.createdAt)}</p>
                  <p><strong>Updated:</strong> {formatDateTime(selectedRental.updatedAt)}</p>
                </Col>
              </Row>
              
              {selectedRental.notes && (
                <>
                  <hr />
                  <h5>Notes</h5>
                  <p>{selectedRental.notes}</p>
                </>
              )}
              
              <hr />
              
              <h5>Update Status</h5>
              <div className="d-flex gap-2 flex-wrap">
                {['PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED'].map(status => (
                  <Button
                    key={status}
                    variant={selectedRental.status === status ? 'primary' : 'outline-primary'}
                    size="sm"
                    disabled={selectedRental.status === status}
                    onClick={() => handleStatusUpdate(selectedRental.id, status)}
                  >
                    {status}
                  </Button>
                ))}
              </div>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AdminDashboard;
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Row, Col, Card, Form, Button, Alert, Spinner, Modal 
} from 'react-bootstrap';
import { Equipment, Customer, RentalRequest } from '../types';
import { equipmentAPI, customerAPI, rentalAPI } from '../services/api';

const RentalForm: React.FC = () => {
  const { equipmentId } = useParams<{ equipmentId: string }>();
  const navigate = useNavigate();
  
  const [equipment, setEquipment] = useState<Equipment | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  
  const [formData, setFormData] = useState({
    // Customer Info
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    
    // Rental Info
    startDate: '',
    endDate: '',
    quantity: 1,
    notes: ''
  });
  
  const [estimatedCost, setEstimatedCost] = useState<number | null>(null);
  const [availableQuantity, setAvailableQuantity] = useState<number | null>(null);

  useEffect(() => {
    if (equipmentId) {
      fetchEquipment(parseInt(equipmentId));
    }
  }, [equipmentId]);

  useEffect(() => {
    if (formData.startDate && formData.endDate && equipment) {
      calculateCost();
      checkAvailability();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formData.startDate, formData.endDate, formData.quantity]);

  const fetchEquipment = async (id: number) => {
    try {
      const response = await equipmentAPI.getWithCategory(id);
      setEquipment(response.data);
    } catch (err) {
      setError('Failed to fetch equipment details');
      console.error('Error fetching equipment:', err);
    } finally {
      setLoading(false);
    }
  };

  const calculateCost = async () => {
    if (!equipment || !formData.startDate || !formData.endDate) return;
    
    try {
      const response = await rentalAPI.calculateCost(
        equipment.id,
        formData.startDate,
        formData.endDate,
        formData.quantity
      );
      setEstimatedCost(response.data);
    } catch (err) {
      console.error('Error calculating cost:', err);
    }
  };

  const checkAvailability = async () => {
    if (!equipment || !formData.startDate || !formData.endDate) return;
    
    try {
      const response = await rentalAPI.getAvailableQuantityForPeriod(
        equipment.id,
        formData.startDate,
        formData.endDate
      );
      setAvailableQuantity(response.data);
    } catch (err) {
      console.error('Error checking availability:', err);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      // First, create or find customer
      let customer: Customer;
      try {
        const existingCustomer = await customerAPI.getByEmail(formData.email);
        customer = existingCustomer.data;
      } catch (err) {
        // Customer doesn't exist, create new one
        const newCustomer = await customerAPI.create({
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          phone: formData.phone,
          address: formData.address,
          city: formData.city,
          state: formData.state,
          zipCode: formData.zipCode
        });
        customer = newCustomer.data;
      }

      // Create rental
      const rentalRequest: RentalRequest = {
        customerId: customer.id,
        equipmentId: equipment!.id,
        startDate: formData.startDate,
        endDate: formData.endDate,
        quantity: formData.quantity,
        notes: formData.notes
      };

      await rentalAPI.create(rentalRequest);
      setShowModal(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit rental booking');
      console.error('Error submitting rental:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleModalClose = () => {
    setShowModal(false);
    navigate('/equipment');
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

  if (!equipment) {
    return <Alert variant="danger">Equipment not found</Alert>;
  }

  const isFormValid = formData.firstName && formData.lastName && formData.email && 
                     formData.phone && formData.startDate && formData.endDate &&
                     formData.quantity > 0;

  const isAvailable = availableQuantity !== null && availableQuantity >= formData.quantity;

  return (
    <div>
      <Button variant="outline-secondary" onClick={() => navigate(-1)} className="mb-3">
        ← Back
      </Button>
      
      <h1>Rent Equipment</h1>
      
      <Row>
        <Col lg={8}>
          <Card>
            <Card.Body>
              <h4>Rental Information</h4>
              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>First Name *</Form.Label>
                      <Form.Control
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Last Name *</Form.Label>
                      <Form.Control
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>
                
                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Email *</Form.Label>
                      <Form.Control
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Phone *</Form.Label>
                      <Form.Control
                        type="tel"
                        name="phone"
                        value={formData.phone}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>
                
                <Form.Group className="mb-3">
                  <Form.Label>Address</Form.Label>
                  <Form.Control
                    type="text"
                    name="address"
                    value={formData.address}
                    onChange={handleInputChange}
                  />
                </Form.Group>
                
                <Row>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>City</Form.Label>
                      <Form.Control
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleInputChange}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>State</Form.Label>
                      <Form.Control
                        type="text"
                        name="state"
                        value={formData.state}
                        onChange={handleInputChange}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>ZIP Code</Form.Label>
                      <Form.Control
                        type="text"
                        name="zipCode"
                        value={formData.zipCode}
                        onChange={handleInputChange}
                      />
                    </Form.Group>
                  </Col>
                </Row>
                
                <hr />
                
                <h5>Rental Period</h5>
                <Row>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Start Date *</Form.Label>
                      <Form.Control
                        type="date"
                        name="startDate"
                        value={formData.startDate}
                        onChange={handleInputChange}
                        min={new Date().toISOString().split('T')[0]}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>End Date *</Form.Label>
                      <Form.Control
                        type="date"
                        name="endDate"
                        value={formData.endDate}
                        onChange={handleInputChange}
                        min={formData.startDate || new Date().toISOString().split('T')[0]}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Quantity *</Form.Label>
                      <Form.Control
                        type="number"
                        name="quantity"
                        value={formData.quantity}
                        onChange={handleInputChange}
                        min="1"
                        max={equipment.totalQuantity}
                        required
                      />
                      {availableQuantity !== null && (
                        <Form.Text className={isAvailable ? 'text-success' : 'text-danger'}>
                          {availableQuantity} available for selected period
                        </Form.Text>
                      )}
                    </Form.Group>
                  </Col>
                </Row>
                
                <Form.Group className="mb-3">
                  <Form.Label>Notes</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="notes"
                    value={formData.notes}
                    onChange={handleInputChange}
                    placeholder="Any special requirements or notes..."
                  />
                </Form.Group>
                
                {error && <Alert variant="danger">{error}</Alert>}
                
                <div className="d-grid">
                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    disabled={!isFormValid || !isAvailable || submitting}
                  >
                    {submitting ? 'Submitting...' : 'Submit Rental Request'}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
        
        <Col lg={4}>
          <Card className="sticky-top">
            <Card.Body>
              <h5>Equipment Summary</h5>
              <div className="text-center mb-3">
                <img
                  src={equipment.imageUrl || 'https://via.placeholder.com/200x150?text=No+Image'}
                  alt={equipment.name}
                  className="img-fluid rounded"
                  style={{ maxHeight: '150px' }}
                />
              </div>
              <h6>{equipment.name}</h6>
              <p className="text-muted small">{equipment.manufacturer} - {equipment.model}</p>
              
              <hr />
              
              <div className="d-flex justify-content-between mb-2">
                <span>Daily Rate:</span>
                <span>${equipment.dailyPrice}</span>
              </div>
              
              {formData.quantity > 1 && (
                <div className="d-flex justify-content-between mb-2">
                  <span>Quantity:</span>
                  <span>{formData.quantity}</span>
                </div>
              )}
              
              {estimatedCost !== null && (
                <>
                  <hr />
                  <div className="d-flex justify-content-between">
                    <strong>Estimated Total:</strong>
                    <strong className="text-primary">${estimatedCost.toFixed(2)}</strong>
                  </div>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
      
      {/* Success Modal */}
      <Modal show={showModal} onHide={handleModalClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>Booking Successful!</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Alert variant="success">
            Your rental request has been submitted successfully. 
            We will contact you soon to confirm the booking details.
          </Alert>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="primary" onClick={handleModalClose}>
            Continue Browsing
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default RentalForm;
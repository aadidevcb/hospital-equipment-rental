import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Row, Col, Card, Button, Badge, Alert, Spinner } from 'react-bootstrap';
import { Equipment } from '../types';
import { equipmentAPI } from '../services/api';

const EquipmentDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [equipment, setEquipment] = useState<Equipment | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchEquipment(parseInt(id));
    }
  }, [id]);

  const fetchEquipment = async (equipmentId: number) => {
    try {
      const response = await equipmentAPI.getWithCategory(equipmentId);
      setEquipment(response.data);
    } catch (err) {
      setError('Failed to fetch equipment details');
      console.error('Error fetching equipment:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusVariant = (status: string) => {
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

  if (!equipment) {
    return <Alert variant="warning">Equipment not found</Alert>;
  }

  return (
    <div>
      <Button variant="outline-secondary" onClick={() => navigate(-1)} className="mb-3">
        ← Back
      </Button>
      
      <Row>
        <Col lg={6}>
          <Card>
            <Card.Img
              variant="top"
              src={equipment.imageUrl || 'https://via.placeholder.com/500x400?text=No+Image'}
              alt={equipment.name}
              style={{ height: '400px', objectFit: 'cover' }}
            />
          </Card>
        </Col>
        
        <Col lg={6}>
          <Card>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-start mb-3">
                <div>
                  <Card.Title className="h2">{equipment.name}</Card.Title>
                  <Card.Subtitle className="mb-2 text-muted">
                    {equipment.manufacturer} - {equipment.model}
                  </Card.Subtitle>
                </div>
                <Badge bg={getStatusVariant(equipment.status)}>
                  {equipment.status}
                </Badge>
              </div>
              
              <Card.Text className="mb-4">
                {equipment.description}
              </Card.Text>
              
              <Row className="mb-4">
                <Col sm={6}>
                  <strong>Category:</strong><br />
                  <span className="text-muted">{equipment.category.name}</span>
                </Col>
                <Col sm={6}>
                  <strong>Daily Price:</strong><br />
                  <span className="h4 text-primary">${equipment.dailyPrice}/day</span>
                </Col>
              </Row>
              
              <Row className="mb-4">
                <Col sm={6}>
                  <strong>Available Quantity:</strong><br />
                  <span className={equipment.availableQuantity > 0 ? 'text-success' : 'text-danger'}>
                    {equipment.availableQuantity} of {equipment.totalQuantity}
                  </span>
                </Col>
                <Col sm={6}>
                  <strong>Manufacturer:</strong><br />
                  <span className="text-muted">{equipment.manufacturer}</span>
                </Col>
              </Row>
              
              <div className="d-grid gap-2">
                <Button
                  variant="primary"
                  size="lg"
                  onClick={() => navigate(`/rent/${equipment.id}`)}
                  disabled={equipment.availableQuantity === 0 || equipment.status !== 'AVAILABLE'}
                >
                  {equipment.availableQuantity > 0 && equipment.status === 'AVAILABLE'
                    ? 'Rent This Equipment'
                    : 'Currently Unavailable'
                  }
                </Button>
                
                <Button
                  variant="outline-primary"
                  onClick={() => navigate('/equipment')}
                >
                  Browse More Equipment
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
      
      {/* Category Description */}
      {equipment.category.description && (
        <Row className="mt-4">
          <Col>
            <Card>
              <Card.Body>
                <Card.Title>About {equipment.category.name}</Card.Title>
                <Card.Text>{equipment.category.description}</Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </div>
  );
};

export default EquipmentDetail;
import React from 'react';
import { Row, Col, Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const Home: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div>
      <div className="bg-light p-5 rounded-lg mb-4">
        <h1 className="display-4">Hospital Equipment Rental</h1>
        <p className="lead">
          Rent high-quality medical equipment for your healthcare needs. 
          From wheelchairs to monitoring devices, we have everything you need.
        </p>
        <Button onClick={() => navigate('/equipment')} variant="primary" size="lg">
          Browse Equipment
        </Button>
      </div>

      <Row>
        <Col md={4}>
          <Card className="h-100">
            <Card.Body>
              <Card.Title>🦽 Mobility Equipment</Card.Title>
              <Card.Text>
                Wheelchairs, walkers, crutches, and other mobility aids to help patients move safely.
              </Card.Text>
              <Button onClick={() => navigate('/equipment?category=mobility')} variant="outline-primary">
                View Mobility Equipment
              </Button>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={4}>
          <Card className="h-100">
            <Card.Body>
              <Card.Title>📊 Monitoring Equipment</Card.Title>
              <Card.Text>
                Blood pressure monitors, pulse oximeters, thermometers for patient monitoring.
              </Card.Text>
              <Button onClick={() => navigate('/equipment?category=monitoring')} variant="outline-primary">
                View Monitoring Equipment
              </Button>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={4}>
          <Card className="h-100">
            <Card.Body>
              <Card.Title>💨 Respiratory Equipment</Card.Title>
              <Card.Text>
                Oxygen concentrators, nebulizers, and other respiratory support equipment.
              </Card.Text>
              <Button onClick={() => navigate('/equipment?category=respiratory')} variant="outline-primary">
                View Respiratory Equipment
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col>
          <Card>
            <Card.Body>
              <Card.Title>How It Works</Card.Title>
              <Row>
                <Col md={3} className="text-center">
                  <h3>1. Browse</h3>
                  <p>Explore our catalog of medical equipment</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3>2. Select</h3>
                  <p>Choose the equipment and rental period</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3>3. Book</h3>
                  <p>Fill out the rental form with your details</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3>4. Receive</h3>
                  <p>Get your equipment delivered or pick it up</p>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
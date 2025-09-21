import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Form, InputGroup, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Equipment, Category } from '../types';
import { equipmentAPI, categoryAPI } from '../services/api';

const EquipmentList: React.FC = () => {
  const [equipment, setEquipment] = useState<Equipment[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [minPrice, setMinPrice] = useState<string>('');
  const [maxPrice, setMaxPrice] = useState<string>('');
  
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    const categoryParam = searchParams.get('category');
    if (categoryParam) {
      setSelectedCategory(categoryParam);
    }
  }, [searchParams]);

  const fetchData = async () => {
    try {
      const [equipmentResponse, categoriesResponse] = await Promise.all([
        equipmentAPI.getAvailable(),
        categoryAPI.getAll()
      ]);
      setEquipment(equipmentResponse.data);
      setCategories(categoriesResponse.data);
    } catch (err) {
      setError('Failed to fetch equipment data');
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      // Fetch base list first (available equipment)
      let base = await equipmentAPI.getAvailable();
      let items = base.data;

      // Apply category filter client-side if selected
      if (selectedCategory) {
        items = items.filter(i => i.category?.name?.toLowerCase() === selectedCategory.toLowerCase());
      }
      // Apply search term filter
      if (searchTerm) {
        const term = searchTerm.toLowerCase();
        items = items.filter(i =>
          i.name.toLowerCase().includes(term) ||
          i.description.toLowerCase().includes(term) ||
          i.manufacturer.toLowerCase().includes(term) ||
          i.model.toLowerCase().includes(term)
        );
      }
      // Apply price bounds
      if (minPrice) {
        const minVal = parseFloat(minPrice);
        if (!isNaN(minVal)) items = items.filter(i => i.dailyPrice >= minVal);
      }
      if (maxPrice) {
        const maxVal = parseFloat(maxPrice);
        if (!isNaN(maxVal)) items = items.filter(i => i.dailyPrice <= maxVal);
      }

      setEquipment(items);
    } catch (err) {
      setError('Failed to search equipment');
      console.error('Error searching equipment:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setSearchTerm('');
    setSelectedCategory('');
    setMinPrice('');
    setMaxPrice('');
    fetchData();
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

  return (
    <div>
      <h1>Available Equipment</h1>
      
      {/* Search and Filter Section */}
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Search</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="text"
                    placeholder="Search equipment..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </InputGroup>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Category</Form.Label>
                <Form.Select
                  value={selectedCategory}
                  onChange={(e) => setSelectedCategory(e.target.value)}
                >
                  <option value="">All Categories</option>
                  {categories.map(category => (
                    <option key={category.id} value={category.name}>
                      {category.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>Min Price</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="$0"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                />
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>Max Price</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="$999"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                />
              </Form.Group>
            </Col>
            <Col md={2} className="d-flex align-items-end">
              <div className="d-grid gap-2 w-100">
                <Button variant="primary" onClick={handleSearch}>
                  Search
                </Button>
                <Button variant="outline-secondary" onClick={handleReset}>
                  Reset
                </Button>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Equipment Grid */}
      {equipment.length === 0 ? (
        <Alert variant="info">No equipment found matching your criteria.</Alert>
      ) : (
        <Row>
          {equipment.map(item => (
            <Col key={item.id} md={4} className="mb-4">
              <Card className="h-100">
                <Card.Img
                  variant="top"
                  src={item.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'}
                  alt={item.name}
                  style={{ height: '200px', objectFit: 'cover' }}
                />
                <Card.Body className="d-flex flex-column">
                  <Card.Title>{item.name}</Card.Title>
                  <Card.Text className="text-muted small">
                    {item.manufacturer} - {item.model}
                  </Card.Text>
                  <Card.Text className="flex-grow-1">
                    {item.description}
                  </Card.Text>
                  <div className="mt-auto">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <span className="h5 text-primary mb-0">
                        ${item.dailyPrice}/day
                      </span>
                      <span className="text-muted">
                        {item.availableQuantity} available
                      </span>
                    </div>
                    <div className="d-grid gap-2">
                      <Button
                        variant="outline-primary"
                        onClick={() => navigate(`/equipment/${item.id}`)}
                      >
                        View Details
                      </Button>
                      <Button
                        variant="primary"
                        onClick={() => navigate(`/rent/${item.id}`)}
                        disabled={item.availableQuantity === 0}
                      >
                        {item.availableQuantity > 0 ? 'Rent Now' : 'Out of Stock'}
                      </Button>
                    </div>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </div>
  );
};

export default EquipmentList;
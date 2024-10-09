import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import '../css/Footer.css';

const Footer = () => {
  return (
    <footer className="footer py-4">
      <Container className="text-center">
        <Row className="justify-content-center">
          <Col md={4} className="mb-3 mb-md-0" style={{ marginRight: '1.5rem' }}>
            <h5>Contact Us</h5>
            <p>
              Address: Brigade Summit, Unit 701, A Block. 7th Floor, GarudacharPalya, Bengaluru, Karnataka 560048 <br />
              Phone: (+91) 8384561230 <br />
              Email: <a href="mailto:info@paynet.com">info@paynet.com</a>
            </p>
          </Col>
          <Col md={4} className="mb-3 mb-md-0">
            <h5>About Us</h5>
            <p>
              At PayNet, we believe that a well-managed payroll system is the backbone of a thriving organization. 
              We provide efficient solutions that streamline your payroll process, allowing you to focus on building a strong and motivated team.
            </p>
          </Col>
        </Row>
        <div className="mt-3">
          <p className="mb-0">&copy; 2024 PayNet. All rights reserved.</p>
        </div>
      </Container>
    </footer>
  );
};

export default Footer;

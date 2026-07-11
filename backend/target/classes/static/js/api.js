///**
// * SmartShip API Utility
// * Since frontend is served by Spring Boot itself at localhost:8080,
// * all API calls go to the same origin — no CORS issues.
// */
//
//const API_BASE = '';  // Same origin — Spring Boot serves both frontend and API
//
//const Api = {
//  headers() {
//    const token = localStorage.getItem('smartship_token');
//    return {
//      'Content-Type': 'application/json',
//      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
//    };
//  },
//
//  async request(method, path, body = null) {
//    const opts = { method, headers: this.headers() };
//    if (body) opts.body = JSON.stringify(body);
//    const res = await fetch(`${API_BASE}/api${path}`, opts);
//    const data = await res.json().catch(() => ({}));
//    if (!res.ok) {
//      const msg = data.message
//        ? data.message
//        : (data.errors ? Object.values(data.errors).join(', ') : `Error ${res.status}`);
//      throw new Error(msg);
//    }
//    return data;
//  },
//
//  get:  (path)       => Api.request('GET',  path),
//  post: (path, body) => Api.request('POST', path, body),
//
//  // Auth
//  login:    (body) => Api.post('/auth/login', body),
//  register: (body) => Api.post('/auth/register', body),
//
//  // Transport Requests
//  createRequest:  (body) => Api.post('/requests', body),
//  getActiveReqs:  ()     => Api.get('/requests/active'),
//  getMyReqs:      ()     => Api.get('/requests/my'),
//  getRequestById: (id)   => Api.get(`/requests/${id}`),
//
//  // Bids
//  placeBid:          (body) => Api.post('/bids', body),
//  getBidsForRequest: (id)   => Api.get(`/bids/request/${id}`),
//  getMyBids:         ()     => Api.get('/bids/my'),
//
//  // Orders
//  selectWinningBid:    (id) => Api.post(`/orders/select-bid/${id}`),
//  getMyOrdersCustomer: ()   => Api.get('/orders/my/customer'),
//  getMyOrdersAsShipper:()   => Api.get('/orders/my/shipper'),
//};
//
//// Session helpers
//const Session = {
//  save(data)    { localStorage.setItem('smartship_token', data.token); localStorage.setItem('smartship_user', JSON.stringify(data)); },
//  get()         { try { return JSON.parse(localStorage.getItem('smartship_user')); } catch { return null; } },
//  clear()       { localStorage.removeItem('smartship_token'); localStorage.removeItem('smartship_user'); },
//  isLoggedIn()  { return !!localStorage.getItem('smartship_token'); },
//  role()        { return this.get()?.role || ''; },
//};
//
//// Toast notifications
//function showToast(msg, type = 'info') {
//  let c = document.querySelector('.toast-container');
//  if (!c) { c = document.createElement('div'); c.className = 'toast-container'; document.body.appendChild(c); }
//  const t = document.createElement('div');
//  t.className = `toast toast-${type}`;
//  t.textContent = msg;
//  c.appendChild(t);
//  setTimeout(() => t.remove(), 3500);
//}
//// Tracking
//updateTracking:      (orderId, body) => Api.post(`/tracking/${orderId}`, body),
//getTracking:         (orderId)       => Api.get(`/tracking/${orderId}`),
//getMyShipments:      ()              => Api.get('/tracking/my/shipments'),
//getMyOrderTracking:  ()              => Api.get('/tracking/my/orders'),
//// Format helpers
//const fmt = {
//  price: (v) => v != null ? `₹${Number(v).toLocaleString('en-IN')}` : '—',
//  date:  (v) => v ? new Date(v).toLocaleDateString('en-IN', {day:'2-digit',month:'short',year:'numeric'}) : '—',
//  hours: (h) => !h ? '—' : h < 24 ? `${h}h` : `${Math.floor(h/24)}d ${h%24 ? h%24+'h' : ''}`.trim(),
//};

const API_BASE = '';

const Api = {
  headers() {
    const token = localStorage.getItem('smartship_token');
    return {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
  },

  async request(method, path, body = null) {
    const opts = { method, headers: this.headers() };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(`${API_BASE}/api${path}`, opts);
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      const msg = data.message
        ? data.message
        : (data.errors ? Object.values(data.errors).join(', ') : `Error ${res.status}`);
      throw new Error(msg);
    }
    return data;
  },

  get:  (path)       => Api.request('GET',  path),
  post: (path, body) => Api.request('POST', path, body),

  // Auth
  login:    (body) => Api.post('/auth/login', body),
  register: (body) => Api.post('/auth/register', body),

  // Transport Requests
  createRequest:  (body) => Api.post('/requests', body),
  getActiveReqs:  ()     => Api.get('/requests/active'),
  getMyReqs:      ()     => Api.get('/requests/my'),
  getRequestById: (id)   => Api.get(`/requests/${id}`),

  // Bids
  placeBid:          (body) => Api.post('/bids', body),
  getBidsForRequest: (id)   => Api.get(`/bids/request/${id}`),
  getMyBids:         ()     => Api.get('/bids/my'),

  // Orders
  selectWinningBid:     (id) => Api.post(`/orders/select-bid/${id}`),
  getMyOrdersCustomer:  ()   => Api.get('/orders/my/customer'),
  getMyOrdersAsShipper: ()   => Api.get('/orders/my/shipper'),

  // Tracking
  updateTracking:     (orderId, body) => Api.post(`/tracking/${orderId}`, body),
  getTracking:        (orderId)       => Api.get(`/tracking/${orderId}`),
  getMyShipments:     ()              => Api.get('/tracking/my/shipments'),
  getMyOrderTracking: ()              => Api.get('/tracking/my/orders'),
};

const Session = {
  save(data)   { localStorage.setItem('smartship_token', data.token); localStorage.setItem('smartship_user', JSON.stringify(data)); },
  get()        { try { return JSON.parse(localStorage.getItem('smartship_user')); } catch { return null; } },
  clear()      { localStorage.removeItem('smartship_token'); localStorage.removeItem('smartship_user'); },
  isLoggedIn() { return !!localStorage.getItem('smartship_token'); },
  role()       { return this.get()?.role || ''; },
};

function showToast(msg, type = 'info') {
  let c = document.querySelector('.toast-container');
  if (!c) { c = document.createElement('div'); c.className = 'toast-container'; document.body.appendChild(c); }
  const t = document.createElement('div');
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

const fmt = {
  price: (v) => v != null ? `₹${Number(v).toLocaleString('en-IN')}` : '—',
  date:  (v) => v ? new Date(v).toLocaleDateString('en-IN', {day:'2-digit',month:'short',year:'numeric'}) : '—',
  hours: (h) => !h ? '—' : h < 24 ? `${h}h` : `${Math.floor(h/24)}d ${h%24 ? h%24+'h' : ''}`.trim(),
};
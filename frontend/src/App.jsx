import { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [file, setFile] = useState(null);
  const [fileId, setFileId] = useState('');
  const [transactionId, setTransactionId] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [dragOver, setDragOver] = useState(false);
  const [rejectionReason, setRejectionReason] = useState('');

  const handleFileChange = (e) => setFile(e.target.files[0]);

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const f = e.dataTransfer.files[0];
    if (f) setFile(f);
  };

  const handleUpload = async () => {
    if (!file) { setMessage('error:Please select a file'); return; }
    setLoading(true);
    setMessage('');
    setRejectionReason('');
    setFileId('');
    setTransactionId('');
    try {
      const formData = new FormData();
      formData.append('file', file);
      const response = await axios.post('/api/orchestrate/process-bill', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const data = response.data;
      if (data.finalStatus === 'SUCCESS') {
        setFileId(data.fileId);
        setTransactionId(data.payment.transactionId);
        setMessage('success:Bill processed and payment completed!');
      } else if (data.finalStatus === 'REJECTED') {
        const reason = data.reason ||
                       data.verification?.reason ||
                       'Bill could not be processed';
        setRejectionReason(reason);
        setMessage('error:Bill was rejected');
      } else {
        setMessage('error:Something went wrong. Please try again.');
      }
    } catch {
      setMessage('error:Upload failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const msgType = message.split(':')[0];
  const msgText = message.split(':')[1];

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="brand">BillPay</div>
        <nav className="nav">
          <a className="nav-link active">Upload</a>
          <a className="nav-link">My Bills</a>
          <a className="nav-link">Payments</a>
          <a className="nav-link">Reports</a>
          <a className="nav-link">Settings</a>
        </nav>
        <div className="sidebar-footer">
          <div className="user-dot">PK</div>
          <div>
            <div className="user-name">Prakash</div>
            <div className="user-role">Admin</div>
          </div>
        </div>
      </aside>

      <main className="main">
        <div className="topbar">
          <div>
            <h1 className="page-title">Upload Bill</h1>
            <p className="page-sub">Submit a bill for automated processing and payment</p>
          </div>
        </div>

        <div className="grid">
          <div className="card">
            <h2 className="card-title">Select Document</h2>
            <div
              className={`dropzone ${dragOver ? 'dragover' : ''} ${file ? 'filled' : ''}`}
              onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
              onDragLeave={() => setDragOver(false)}
              onDrop={handleDrop}
              onClick={() => document.getElementById('fi').click()}
            >
              <input id="fi" type="file" onChange={handleFileChange} accept="image/*,.pdf" style={{ display: 'none' }} />
              {file ? (
                <div className="file-info">
                  <div className="file-thumb">PDF</div>
                  <div>
                    <div className="file-name">{file.name}</div>
                    <div className="file-meta">{(file.size / 1024).toFixed(1)} KB</div>
                  </div>
                </div>
              ) : (
                <div className="drop-prompt">
                  <div className="drop-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="17 8 12 3 7 8"/>
                      <line x1="12" y1="3" x2="12" y2="15"/>
                    </svg>
                  </div>
                  <p className="drop-title">Drop file here or click to upload</p>
                  <p className="drop-hint">Supports JPG, PNG, PDF up to 10MB</p>
                </div>
              )}
            </div>

            {message && (
              <div className={`alert ${msgType}`}>
                {msgText}
                {rejectionReason && (
                  <div className="rejection-reason">
                    Reason: {rejectionReason}
                  </div>
                )}
              </div>
            )}

            <button className="btn-upload" onClick={handleUpload} disabled={loading}>
              {loading ? 'Processing...' : 'Submit Bill'}
            </button>
          </div>

          <div className="card">
            <h2 className="card-title">Processing Status</h2>
            <div className="pipeline">
              {[
                { label: 'Upload', desc: 'File received and stored', status: fileId ? 'done' : 'idle' },
                { label: 'AI Extraction', desc: 'Reading bill details', status: fileId ? 'done' : 'idle' },
                { label: 'Verification', desc: 'Validating amount and vendor', status: fileId ? 'done' : 'idle' },
                { label: 'Payment', desc: 'Processing transaction', status: fileId ? 'done' : 'idle' },
                { label: 'Notification', desc: 'Confirmation sent', status: fileId ? 'done' : 'idle' },
              ].map((step, i) => (
                <div className={`pipeline-step ${step.status}`} key={i}>
                  <div className="step-num">{i + 1}</div>
                  <div className="step-body">
                    <div className="step-label">{step.label}</div>
                    <div className="step-desc">{step.desc}</div>
                  </div>
                  <div className={`step-badge ${step.status}`}>
                    {step.status === 'done' ? 'Done' : 'Pending'}
                  </div>
                </div>
              ))}
            </div>

            {transactionId && (
              <div className="id-box">
                <span className="id-label">Transaction Reference</span>
                <code className="id-value">{transactionId}</code>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
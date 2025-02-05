// Import our custom CSS
import '../scss/styles.scss'

// Import all of Bootstrap's JS
import * as bootstrap from 'bootstrap'

document.getElementById('add-ips-btn').addEventListener('click', function(event) { 
    const newIpsTextarea = document.getElementById('new-ips');
    const currentIpsTextarea = document.getElementById('current-ips');
    
    const newIps = newIpsTextarea.value
      .split('\n')
      .map(ip => ip.trim())
      .filter(ip => ip.length > 0);
  
    if (newIps.length === 0) return;
  
    const currentIps = currentIpsTextarea.value
      .split('\n')
      .map(ip => ip.trim())
      .filter(ip => ip.length > 0);
  
    const uniqueIps = [...new Set([...currentIps, ...newIps])];
    
    currentIpsTextarea.value = uniqueIps.join('\n');
    
    newIpsTextarea.value = '';
  });
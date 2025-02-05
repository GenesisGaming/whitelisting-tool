// Import our custom CSS
import '../scss/styles.scss';

// Import all of Bootstrap's JS
import * as bootstrap from 'bootstrap';

document.addEventListener( 'DOMContentLoaded', function () {
	const addIpsBtn = document.getElementById( 'add-ips-btn' );
	const newIpsTextarea = document.getElementById( 'new-ips' );
	const currentIpsTextarea = document.getElementById( 'current-ips' );
	const partnerSelect = document.getElementById( 'partner' );
	const commentsTextarea = document.getElementById( 'comments' );

	// validate IP address
	function isValidIp ( ip ) {
		const ipRegex = /^(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})$/;
		return ipRegex.test( ip );
	}

	// handle adding IPs
	addIpsBtn.addEventListener( 'click', function () {
		const newIps = newIpsTextarea.value
			.split( '\n' )
			.map( ip => ip.trim() )
			.filter( ip => ip.length > 0 );

		if ( newIps.length === 0 ) return;

		// validate each IP
		const invalidIps = newIps.filter( ip => !isValidIp( ip ) );

		if ( invalidIps.length > 0 ) {
			console.error( `Invalid IP(s) detected: ${ invalidIps.join( ', ' ) }` );
			return;
		}

		const currentIps = currentIpsTextarea.value
			.split( '\n' )
			.map( ip => ip.trim() )
			.filter( ip => ip.length > 0 );

		const uniqueIps = Array.from( new Set( [ ...currentIps, ...newIps ] ) );

		currentIpsTextarea.value = uniqueIps.join( '\n' );

		newIpsTextarea.value = '';
		newIpsTextarea.focus();

		const selectedPartner = partnerSelect.value || null;
		const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.id;
		const comments = commentsTextarea.value.trim();

		// create the log object
		const logObject = {
			partner: selectedPartner,
			whitelisting: selectedWhitelist,
			ips: uniqueIps,
			comments: comments
		};

		// Print the validated object
		console.log( logObject );
	} );
} );

// Import our custom CSS
import '../scss/styles.scss';

// Import all of Bootstrap's JS
import * as bootstrap from 'bootstrap';

document.addEventListener( 'DOMContentLoaded', function () {
	const addIpsBtn = document.getElementById( 'add-ips-btn' );
	const newIpsTextarea = document.getElementById( 'new-ips' );
	const currentIpsTextarea = document.getElementById( 'current-ips' );
	const partnerInput = document.getElementById( 'partner' );
	const dropdown = document.getElementById( 'partner-dropdown' );
	const commentsTextarea = document.getElementById( 'comments' );

	// Mock data for partners input
	const partners = [
		"Partner A",
		"Partner B",
		"Partner C",
		"Partner D",
		"Partner E",
		"Partner F"
	];

	// Event listener for user input to filter dropdown
	partnerInput.addEventListener( 'input', function () {
		const query = partnerInput.value.toLowerCase();
		const filteredPartners = partners.filter( partner => partner.toLowerCase().includes( query ) );

		// Display matching options
		dropdown.innerHTML = "";
		filteredPartners.forEach( partner => {
			const listItem = document.createElement( "li" );
			listItem.classList.add( "dropdown-item" );
			listItem.textContent = partner;

			listItem.addEventListener( "click", function () {
				partnerInput.value = partner;
				dropdown.style.display = "none";  // Hide dropdown on select
			} );

			dropdown.appendChild( listItem );
		} );

		// Show dropdown if matches are found
		dropdown.style.display = filteredPartners.length > 0 ? "block" : "none";
	} );

	// Event listener to open the dropdown when clicking the input field
	partnerInput.addEventListener( 'click', function () {
		const query = partnerInput.value.toLowerCase();
		const filteredPartners = partners.filter( partner => partner.toLowerCase().includes( query ) );

		// Show dropdown even if no text is typed (show all items)
		dropdown.innerHTML = "";
		filteredPartners.forEach( partner => {
			const listItem = document.createElement( "li" );
			listItem.classList.add( "dropdown-item" );
			listItem.textContent = partner;

			listItem.addEventListener( "click", function () {
				partnerInput.value = partner;
				dropdown.style.display = "none";  // Hide dropdown on select
			} );

			dropdown.appendChild( listItem );
		} );

		dropdown.style.display = "block"; // Always show the dropdown on click
	} );

	// Hide the dropdown if the user clicks outside
	document.addEventListener( "click", function ( event ) {
		if ( !event.target.closest( ".dropdown-container" ) ) {
			dropdown.style.display = "none";
		}
	} );

	// Validate IP address
	function isValidIp ( ip ) {
		const ipRegex = /^(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})$/;
		return ipRegex.test( ip );
	}

	// Handle adding IPs
	addIpsBtn.addEventListener( 'click', function () {
		const newIps = newIpsTextarea.value
			.split( '\n' )
			.map( ip => ip.trim() )
			.filter( ip => ip.length > 0 );

		if ( newIps.length === 0 ) return;

		// Validate each IP
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

		const selectedPartner = partnerInput.value || null;
		const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.id;
		const comments = commentsTextarea.value.trim();

		// Create the log object
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

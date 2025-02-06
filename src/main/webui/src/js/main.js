import '../scss/styles.scss';
import * as bootstrap from 'bootstrap';

const BASE_URL = "http://localhost:3000";
const GET = 'GET';
const POST = 'POST';

document.addEventListener( 'DOMContentLoaded', function () {
	const addIpsBtn = document.getElementById( 'add-ips-btn' );
	const addPartnerBtn = document.getElementById( 'add-partner-btn' );
	const newIpsTextarea = document.getElementById( 'new-ips' );
	const currentIpsTextarea = document.getElementById( 'current-ips' );
	const partnerInput = document.getElementById( 'partner' );
	const dropdown = document.getElementById( 'partner-dropdown' );
	const commentsTextarea = document.getElementById( 'comments' );
	const whitelistRadios = document.querySelectorAll( 'input[name="whitelistType"]' );

	// Mock data for partners input / TO DO: use API to populate
	const partners = [ "Partner A", "Partner B", "Partner C", "Partner D", "Partner E", "Partner F" ];

	const disableFields = () => {
		whitelistRadios.forEach( radio => radio.disabled = true );
		newIpsTextarea.disabled = true;
		commentsTextarea.disabled = true;
		addIpsBtn.disabled = true;
		addPartnerBtn.disabled = true;
	};

	const clearFields = () => {
		newIpsTextarea.value = '';
		commentsTextarea.value = '';
		currentIpsTextarea.value = '';
		whitelistRadios.forEach( radio => radio.checked = false );
	};

	const enableFields = () => {
		whitelistRadios.forEach( radio => radio.disabled = false );
		addPartnerBtn.disabled = true;
	};

	const enableIpsAndComments = () => {
		newIpsTextarea.disabled = false;
		commentsTextarea.disabled = false;
	};

	const enableAddButton = () => {
		addIpsBtn.disabled = newIpsTextarea.value.trim() === '' || commentsTextarea.value.trim() === '';
	};

	const sendRequest = async ( method, path, payload ) => {
		try {
			const response = await fetch( `${ BASE_URL }${ path }`, {
				method: method,
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify( payload )
			} );

			const result = await response.json();

			if ( response.ok ) {
				alert( `Success: ${ result.message || "Request completed successfully!" }` );
			} else {
				alert( `Error: ${ result.error || "Something went wrong!" }` );
			}
		} catch ( error ) {
			alert( `Request failed: ${ error.message }` );
		}
	};

	// Initial disable of fields
	disableFields();

	// Event listener for typing in partner input
	partnerInput.addEventListener( 'input', function () {
		const query = partnerInput.value.trim().toLowerCase();
		const isPartnerValid = partners.some( partner => partner.toLowerCase() === query );
		console.log( query );

		if ( query === "" || isPartnerValid ) {
			addPartnerBtn.disabled = true;
		} else {
			disableFields();
			clearFields();
			addPartnerBtn.disabled = false;
		}

		// Handle dropdown filtering
		const filteredPartners = partners.filter( partner => partner.toLowerCase().includes( query ) );
		dropdown.innerHTML = "";
		filteredPartners.forEach( partner => {
			const listItem = document.createElement( "li" );
			listItem.classList.add( "dropdown-item" );
			listItem.textContent = partner;

			listItem.addEventListener( "click", function () {
				partnerInput.value = partner;
				dropdown.style.display = "none";
				enableFields();
			} );

			dropdown.appendChild( listItem );
		} );

		dropdown.style.display = filteredPartners.length > 0 ? "block" : "none";
	} );

	// Event listener to open the dropdown when clicking the input field
	partnerInput.addEventListener( 'click', function () {
		const query = partnerInput.value.trim().toLowerCase();
		const filteredPartners = partners.filter( partner => partner.toLowerCase().includes( query ) );

		dropdown.innerHTML = "";
		filteredPartners.forEach( partner => {
			const listItem = document.createElement( "li" );
			listItem.classList.add( "dropdown-item" );
			listItem.textContent = partner;

			listItem.addEventListener( "click", function () {
				partnerInput.value = partner;
				dropdown.style.display = "none";
				enableFields();
				clearFields();
			} );

			dropdown.appendChild( listItem );
		} );

		dropdown.style.display = "block";
	} );

	// Event listener for selecting a whitelist radio button
	whitelistRadios.forEach( radio => {
		radio.addEventListener( 'change', function () {
			enableIpsAndComments();
		} );
	} );

	// Event listener for the New IPs and Comments fields to enable the Add button
	newIpsTextarea.addEventListener( 'input', enableAddButton );
	commentsTextarea.addEventListener( 'input', enableAddButton );

	document.addEventListener( "click", function ( event ) {
		if ( !event.target.closest( ".dropdown-container" ) ) {
			dropdown.style.display = "none";
		}
	} );

	// Validate IP address or IP/CIDR range
	function isValidIp ( ip ) {
		const ipRegex = /^(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})\.(25[0-5]|2[0-4][0-9]|1\d{2}|\d{1,2})(\/(3[0-2]|[1-2]?[0-9]))?$/;
		return ipRegex.test( ip );
	}

	// Handle adding a partner
	addPartnerBtn.addEventListener( 'click', function () {
		const partnerName = partnerInput.value.trim();

		if ( partnerName === '' ) return;

		const confirmAdd = confirm( `Are you sure you want to add "${ partnerName }" as a new partner?` );

		if ( confirmAdd ) {
			partners.push( partnerName ); // Add to the partner list / TO DO: Remove and refresh from API
			partnerInput.value = '';
			addPartnerBtn.disabled = true;
			console.log( `Partner "${ partnerName }" added.` );
			sendRequest( POST, '/add-partner', { partnerName } );
		}
	} );

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
			alert( `Invalid IP(s) detected:\n${ invalidIps.join( '\n' ) }` );
			return;
		}

		const confirmAdd = confirm( `Are you sure you want to add the following IP(s)?\n\n${ newIps.join( '\n' ) }` );

		if ( confirmAdd ) {
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

			const logObject = {
				partner: selectedPartner,
				whitelisting: selectedWhitelist,
				ips: uniqueIps,
				comments: comments
			};

			addIpsBtn.disabled = true;

			console.log( logObject ); //TO DO: Handle logging optional
			sendRequest( POST, '/add-ips', logObject );
		}
	} );
} );

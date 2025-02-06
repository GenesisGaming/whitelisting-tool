import '../scss/styles.scss';
import * as bootstrap from 'bootstrap';

const BASE_URL = "http://localhost:8080";
const GET = 'GET';
const POST = 'POST';

document.addEventListener( 'DOMContentLoaded', async function () {
	const addIpsBtn = document.getElementById( 'add-ips-btn' );
	const addPartnerBtn = document.getElementById( 'add-partner-btn' );
	const newIpsTextarea = document.getElementById( 'new-ips' );
	const currentIpsTextarea = document.getElementById( 'current-ips' );
	const partnerInput = document.getElementById( 'partner' );
	const dropdown = document.getElementById( 'partner-dropdown' );
	const commentsTextarea = document.getElementById( 'comments' );
	const whitelistRadios = document.querySelectorAll( 'input[name="whitelistType"]' );

	let partners = [];

	const sendRequest = async ( method, path, payload = null ) => {
		try {
			const response = await fetch( `${ BASE_URL }${ path }`, {
				method,
				headers: { 'Content-Type': 'application/json' },
				body: method !== GET ? JSON.stringify( payload ) : null
			} );

			const result = await response.json();

			if ( !response.ok ) {
				throw new Error( result.error || "Something went wrong!" );
			}

			return result;
		} catch ( error ) {
			alert( `Request failed: ${ error.message }` );
			throw error;
		}
	};

	// Function to fetch partners from API
	// Function to fetch partners using sendRequest()
	const fetchPartners = async () => {
		try {
			const partnersData = await sendRequest( GET, '/operator' );
			partners = partnersData.map( partner => partner.code ); // Extract names
		} catch ( error ) {
			console.error( "Failed to fetch partners:", error );
		}
	};

	// Function to fetch IPs for a specific partner
	const fetchIpsForPartner = async ( partnerName ) => {
		try {
			const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.value;
			const ips = await sendRequest( GET, `/operator/${ partnerName }/ip?whitelistType=${ selectedWhitelist }` );
			// Update the currentIpsTextarea with each IP on a new line
			currentIpsTextarea.value = ips.join( "\n" );
		} catch ( error ) {
			console.error( "Failed to fetch IPs:", error );
		}
	};

	// Fetch partners on page load
	await fetchPartners();

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

	disableFields();

	// Partner input event listener
	partnerInput.addEventListener( 'input', function () {
		const query = partnerInput.value.trim().toLowerCase();
		const isPartnerValid = partners.some( partner => partner.toLowerCase() === query );

		if ( query === "" || isPartnerValid ) {
			addPartnerBtn.disabled = true;
		} else {
			disableFields();
			clearFields();
			addPartnerBtn.disabled = false;
		}

		// Populate dropdown
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

	// Show dropdown when clicking input
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
				disableFields();
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
			const selectedPartner = partnerInput.value.trim();

			if ( selectedPartner ) {
				fetchIpsForPartner( selectedPartner );
			}
		} );
	} );

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
	addPartnerBtn.addEventListener( 'click', async function () {
		const partnerName = partnerInput.value.trim();
		if ( partnerName === '' ) return;

		const confirmAdd = confirm( `Are you sure you want to add "${ partnerName }" as a new partner?` );
		if ( confirmAdd ) {
			await sendRequest( POST, '/operator', { "code": partnerName } );
			await fetchPartners(); // Refresh partners list
		}
	} );

	// Handle adding IPs
	addIpsBtn.addEventListener( 'click', async function () {
		const newIps = newIpsTextarea.value
			.split( '\n' )
			.map( ip => ip.trim() )
			.filter( ip => ip.length > 0 );

		if ( newIps.length === 0 ) return;

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

			const selectedPartner = partnerInput.value.trim() || null;
			const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.value;
			const comments = commentsTextarea.value.trim();

			const logObject = {
				partner: selectedPartner,
				whitelisting: selectedWhitelist,
				ips: uniqueIps,
				comments: comments
			};

			addIpsBtn.disabled = true;

			await sendRequest( POST, `/operator/${ selectedPartner }/ip`, { "whitelistType": selectedWhitelist.toUpperCase(), "newIps": newIps } );
			await fetchIpsForPartner( selectedPartner );
		}
	} );
} );

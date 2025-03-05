import '../scss/styles.scss';
import * as bootstrap from 'bootstrap';

const DISABLE_API_WL = true;

const STAGING_HOST = "whitelist.star9ad.com";
const GET = 'GET';
const POST = 'POST';
const PATCH = 'PATCH';
const ADD_BTN_TEXT = 'ADD >>';
const REMOVE_BTN_TEXT = 'REMOVE';
const NEW_IPS_ADDITION_LABEL = 'IPs to ADD';
const NEW_IPS_REMOVAL_LABEL = 'IPs to REMOVE';
const UPDATE_TYPES = { ADDITION: "ADDITION", REMOVAL: "REMOVAL" };

document.addEventListener( 'DOMContentLoaded', async function () {
	const ipActionBtn = document.getElementById( 'ip-action-btn' );
	const addPartnerBtn = document.getElementById( 'add-partner-btn' );
	const newIpsTextarea = document.getElementById( 'new-ips' );
	const currentIpsTextarea = document.getElementById( 'current-ips' );
	const partnerInput = document.getElementById( 'partner' );
	const dropdown = document.getElementById( 'partner-dropdown' );
	const commentsTextarea = document.getElementById( 'comments' );
	const whitelistRadios = document.querySelectorAll( 'input[name="whitelistType"]' );
	const addIpsOption = document.getElementById( 'add-ips-option' );
	const removeIpsOption = document.getElementById( 'remove-ips-option' );
	const newIpsLabel = document.getElementById( 'new-ips-label' );
	const boWhitelistRadio = document.getElementById( 'bo' );
	const apiWhitelistRadio = document.getElementById( 'api' );
	const baseUrl = window.location.href;

	const isStaging = () => {
		return window.location.hostname === STAGING_HOST;
	}

	// Function to toggle the button text and styling
	const updateIpActionButton = () => {
		if ( addIpsOption.checked ) {
			ipActionBtn.textContent = ADD_BTN_TEXT;
			ipActionBtn.classList.remove( "btn-danger" );
			ipActionBtn.classList.add( "btn-success" );
			newIpsLabel.innerHTML = NEW_IPS_ADDITION_LABEL;
		} else {
			ipActionBtn.textContent = REMOVE_BTN_TEXT;
			ipActionBtn.classList.remove( "btn-success" );
			ipActionBtn.classList.add( "btn-danger" );
			newIpsLabel.innerHTML = NEW_IPS_REMOVAL_LABEL;
		}
	};

	addIpsOption.addEventListener( "change", updateIpActionButton );
	removeIpsOption.addEventListener( "change", updateIpActionButton );

	// Initialize correct button state
	updateIpActionButton();

	let partners = [];

	const sendRequest = async ( method, path, payload = null ) => {
		try {
			const response = await fetch( `${ baseUrl }${ path }`, {
				method,
				headers: { 'Content-Type': 'application/json' },
				credentials: 'include',
				body: method !== GET ? JSON.stringify( payload ) : null
			} );

			const text = await response.text();
			const result = text ? JSON.parse( text ) : {}; // Handle empty response gracefully

			if ( !response.ok ) {
				throw new Error( result.error || "Something went wrong!" );
			}

			return result;
		} catch ( error ) {
			alert( `Request failed: ${ error.message }` );
			throw error;
		}
	};

	// Function to fetch operators from API
	// Function to fetch operators using sendRequest()
	const fetchPartners = async () => {
		try {
			const partnersData = await sendRequest( GET, 'operator' );
			partners = partnersData.map( partner => partner.code ); // Extract names
		} catch ( error ) {
			console.error( "Failed to fetch operators:", error );
		}
	};

	// Function to fetch IPs for a specific partner
	const fetchIpsForPartner = async ( partnerName ) => {
		try {
			const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.value;
			const ips = await sendRequest( GET, `operator/${ partnerName }/ip-list?whitelistType=${ selectedWhitelist }` );
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
		ipActionBtn.disabled = true;
		addPartnerBtn.disabled = true;
	};

	const clearFields = () => {
		newIpsTextarea.value = '';
		commentsTextarea.value = '';
		currentIpsTextarea.value = '';
		whitelistRadios.forEach( radio => radio.checked = false );
		DISABLE_API_WL && ( boWhitelistRadio.checked = true );
	};

	const enableFields = () => {
		whitelistRadios.forEach( radio => radio.disabled = false );
		addPartnerBtn.disabled = true;
		DISABLE_API_WL && ( apiWhitelistRadio.disabled = true );
		DISABLE_API_WL && ( boWhitelistRadio.checked = true );
		DISABLE_API_WL && enableIpsAndComments();
	};

	const enableIpsAndComments = () => {
		newIpsTextarea.disabled = false;
		commentsTextarea.disabled = false;
	};

	const enableAddButton = () => {
		ipActionBtn.disabled = newIpsTextarea.value.trim() === '' || commentsTextarea.value.trim() === '';
	};

	const isRemove = () => {
		return removeIpsOption.checked && ipActionBtn.textContent == REMOVE_BTN_TEXT && newIpsLabel.innerHTML == NEW_IPS_REMOVAL_LABEL;
	}

	disableFields();

	// Operator input event listener
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

		const confirmAdd = confirm( `Are you sure you want to add "${ partnerName }" as a new operator?` );
		if ( confirmAdd ) {
			await sendRequest( POST, 'operator', { "code": partnerName } );
			await fetchPartners(); // Refresh partners list
		}
	} );

	// Handle adding IPs
	ipActionBtn.addEventListener( 'click', async function () {
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

		const currentIps = currentIpsTextarea.value
			.split( '\n' )
			.map( ip => ip.trim() )
			.filter( ip => ip.length > 0 );

		const selectedPartner = partnerInput.value.trim() || null;
		const selectedWhitelist = document.querySelector( 'input[name="whitelistType"]:checked' )?.value;
		const comments = commentsTextarea.value.trim();

		if ( isRemove() ) {
			// Remove case: Check if any of the IPs do not exist
			const ipsToRemoveNotWhitelisted = newIps.filter( ip => !currentIps.includes( ip ) );
			if ( ipsToRemoveNotWhitelisted.length > 0 ) {
				alert( `The following IPs you are trying to remove are NOT whitelisted:\n${ ipsToRemoveNotWhitelisted.join( '\n' ) }` );
				return;
			}

			const confirmRemove = confirm( `Are you sure you want to remove the following IP(s)?\n\n${ newIps.join( '\n' ) }` );
			if ( confirmRemove ) {
				await sendRequest( PATCH, `operator/${ selectedPartner }/ip-list`, { "whitelistType": selectedWhitelist.toUpperCase(), "updateType": UPDATE_TYPES.REMOVAL, "ips": newIps, "comments": comments } );
				await fetchIpsForPartner( selectedPartner );
			}
		} else {
			// Add case: Check if any of the IPs already exist
			const ipsToAddAlreadyWhitelisted = newIps.filter( ip => currentIps.includes( ip ) );
			if ( ipsToAddAlreadyWhitelisted.length > 0 ) {
				alert( `The following IPs already exist:\n${ ipsToAddAlreadyWhitelisted.join( '\n' ) }` );
				return;
			}

			const confirmAdd = confirm( `Are you sure you want to add the following IP(s)?\n\n${ newIps.join( '\n' ) }` );
			if ( confirmAdd ) {
				await sendRequest( PATCH, `operator/${ selectedPartner }/ip-list`, { "whitelistType": selectedWhitelist.toUpperCase(), "updateType": UPDATE_TYPES.ADDITION, "ips": newIps, "comments": comments } );
				await fetchIpsForPartner( selectedPartner );
			}
		}

		ipActionBtn.disabled = true;
	} );
} );

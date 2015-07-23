var geolocationUrl = 'http://121.42.25.186:8001/loc';

var deviceOrientation = new ol.DeviceOrientation();

// Geolocation marker
var iconFeature = new ol.Feature({
	geometry: new ol.geom.Point([0, 0]),
});
var iconStyle = new ol.style.Style({
	image: new ol.style.Icon({
		anchor: [0.5, 25],
		anchorYUnits: 'pixels',
		src: '../img/geolocation_marker_heading.png'
	})
});
iconFeature.setStyle(iconStyle);
var vectorSource = new ol.source.Vector({
  features: [iconFeature]
});
var vectorLayer = new ol.layer.Vector({
  source: vectorSource
});
map.addLayer(vectorLayer);

// tracking heading
deviceOrientation.setTracking(true);
deviceOrientation.on('change:heading', function(event) {
	var heading = deviceOrientation.getHeading() || 0;
	iconFeature.getStyle().getImage().setRotation(2 * Math.PI - heading);
});

function geolocation()
{
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
		{
			var pos = JSON.parse(xmlhttp.responseText);
			iconFeature.getGeometry().setCoordinates([pos[0] * 520, pos[1] * 520]);
		}
	}
	xmlhttp.open("GET", geolocationUrl, true);
	xmlhttp.send();
	var t = setTimeout("geolocation();", 1000);
}

function setGeolocationUrl(url)
{
	// start geolocation
	geolocationUrl = url;
	geolocation();
}
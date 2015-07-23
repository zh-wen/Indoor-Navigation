
// 监听点击地图事件
map.on('click', function(evt) {
	var mapWidth = 520;
	var mapHeight = 520;
	if (currentFloor == 0)
		mapHeight = 197.5;
	var pos = map.getEventCoordinate(evt.originalEvent);
	if (pos[0] > 0 && pos[0] < mapWidth && pos[1] > 0 && pos[1] < mapHeight)
	{
		alert([pos[0], pos[1], currentFloor]);
	}
})
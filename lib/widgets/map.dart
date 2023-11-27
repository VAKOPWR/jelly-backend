import 'dart:async';
// ignore: unused_import
import 'dart:developer';
import 'dart:io';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:project_jelly/misc/geocoding.dart';
import 'package:project_jelly/pages/helper/loading.dart';
import 'package:project_jelly/service/map_service.dart';
import 'package:project_jelly/service/snackbar_service.dart';
import 'package:project_jelly/service/visibility_service.dart';
import 'package:project_jelly/widgets/nav_buttons.dart';
import 'package:project_jelly/widgets/marker_info_box.dart';

class MapWidget extends StatefulWidget {
  const MapWidget({super.key});

  @override
  State<MapWidget> createState() => _MapWidgetState();
}

class _MapWidgetState extends State<MapWidget> with WidgetsBindingObserver {
  final Completer<GoogleMapController> _mapController = Completer();
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  late Timer _stateTimer;
  late Timer _markersTimer;
  late Timer _debounce;
  MapType _mapType = MapType.normal;
  String _locationName = "The Earth";

  @override
  void initState() {
    Get.find<SnackbarService>().checkLocationAccess();
    _markersTimer = Timer.periodic(Duration(seconds: 3), (timer) async {
      await Get.find<MapService>().fetchFriendsData();
      await Get.find<MapService>().updateMarkers();
    });
    _stateTimer = Timer.periodic(Duration(milliseconds: 1), (timer) async {
      setState(() {});
    });
    _debounce = Timer(Duration(seconds: 1), () {});
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    Get.find<MapService>().startPositionStream();
  }

  MapType getNextMap(MapType currentMapType) {
    switch (currentMapType) {
      case MapType.normal:
        return MapType.satellite;
      case MapType.satellite:
        return MapType.normal;
      case MapType.none:
        return MapType.normal;
      case MapType.hybrid:
        return MapType.normal;
      case MapType.terrain:
        return MapType.normal;
    }
  }

  @override
  void didChangePlatformBrightness() {
    super.didChangePlatformBrightness();
    Brightness brightness =
        View.of(context).platformDispatcher.platformBrightness;
    if (brightness == Brightness.light) {
      _mapController.future.then(
          (value) => value.setMapStyle(GetStorage().read('lightMapStyle')));
    } else {
      _mapController.future.then(
          (value) => value.setMapStyle(GetStorage().read('darkMapStyle')));
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      Get.find<SnackbarService>().checkLocationAccess();
      Get.find<LocationService>().updateMarkers();
      Get.find<LocationService>().loadImageProviders();
      setState(() {});
    }
  }

  void hideBottomSheet(CameraPosition) {
    setState(() {
      if (Get.find<VisibilitySevice>().isInfoSheetVisible) {
        Get.find<VisibilitySevice>().isInfoSheetVisible = false;
      }
      if (Get.find<VisibilitySevice>().isBottomSheetOpen) {
        Navigator.of(context).pop();
        Get.find<VisibilitySevice>().isBottomSheetOpen = false;
      }
    });
  }

  void _onCameraMove(CameraPosition position) {
    if (_debounce.isActive) {
      _debounce.cancel();
    }
    _debounce = Timer(const Duration(milliseconds: 500), () async {
      GoogleMapController controller = await _mapController.future;
      double zoomLevel = await controller.getZoomLevel();
      if (zoomLevel <= 6) {
        setState(() {
          _locationName = "The Earth";
        });
      } else {
        String newCityName =
            await getCityNameFromCoordinates(position.target, zoomLevel);
        setState(() {
          _locationName = newCityName;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        key: _scaffoldKey,
        body: Get.find<MapService>().getCurrentLocation() == null
            ? BasicLoadingPage()
            : Stack(children: [
                GoogleMap(
                    compassEnabled: false,
                    rotateGesturesEnabled: false,
                    tiltGesturesEnabled: false,
                    onTap: hideBottomSheet,
                    initialCameraPosition: CameraPosition(
                      target: LatLng(
                        Get.find<MapService>().getCurrentLocation()!.latitude,
                        Get.find<MapService>().getCurrentLocation()!.longitude,
                      ),
                      zoom: 13,
                    ),
                    onMapCreated: (mapController) {
                      if (Theme.of(context).brightness == Brightness.light) {
                        mapController
                            .setMapStyle(GetStorage().read('lightMapStyle'));
                      } else {
                        mapController
                            .setMapStyle(GetStorage().read('darkMapStyle'));
                      }
                      _mapController.complete(mapController);
                    },
                    myLocationButtonEnabled: true,
                    myLocationEnabled: true,
                    padding: EdgeInsets.only(bottom: 100, left: 0, top: 40),
                    mapType: _mapType,
                    markers: Get.find<LocationService>().markers.values.toSet(),
                    onCameraMove: _onCameraMove),
                AnimatedContainer(
                    duration: Duration(milliseconds: 300), // Animation duration
                    height: Get.find<VisibilitySevice>().isInfoSheetVisible
                        ? MediaQuery.of(context).size.height * 0.78
                        : 0,
                    width: MediaQuery.of(context).size.width * 0.9,
                    margin: EdgeInsets.fromLTRB(
                        MediaQuery.of(context).size.width * 0.05, 0, 0, 0),
                    child: Get.find<VisibilitySevice>().highlightedMarker !=
                            null
                        ? MarkerInfoBox(
                            isStaticMarker: Get.find<VisibilitySevice>()
                                    .highlightedMarkerType !=
                                null,
                            id: Get.find<VisibilitySevice>().highlightedMarker!,
                            markerType: Get.find<VisibilitySevice>()
                                .highlightedMarkerType,
                          )
                        : null),
                Platform.isIOS
                    ? Positioned(
                        top: 50.0,
                        right: 10.0,
                        child: FloatingActionButton(
                            onPressed: () {
                              setState(() {
                                _mapType = getNextMap(_mapType);
                              });
                            },
                            child: Icon(
                              Icons.map_rounded,
                              color: Colors.grey[700],
                            ),
                            backgroundColor: Colors.grey[50]),
                      )
                    : Positioned(
                        top: 100.0,
                        right: 12.0,
                        child: SizedBox(
                          height: 38,
                          width: 38,
                          child: FloatingActionButton(
                            heroTag: 'mapTypeButton',
                            onPressed: () {
                              setState(() {
                                _mapType = getNextMap(_mapType);
                              });
                            },
                            child: Icon(
                              Icons.map_rounded,
                              color: Colors.grey[700],
                            ),
                            backgroundColor: Colors.white.withOpacity(0.95),
                            elevation: 2.0,
                            shape: RoundedRectangleBorder(
                              borderRadius:
                                  BorderRadius.all(Radius.circular(0)),
                            ),
                          ),
                        )),
                NavButtons(),
                Positioned(
                  top: 120.0,
                  right: 10.0,
                  child: FloatingActionButton(
                      heroTag: 'placeIconButton',
                      onPressed: () {
                        _showMarkerListBottomSheet();
                      },
                      child: Icon(
                        Icons.place_rounded,
                        color: Colors.grey[700],
                      ),
                      backgroundColor: Colors.grey[50]),
                ),
                Positioned(
                  top: 60.0,
                  left: 25.0,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(
                        _locationName,
                        style: TextStyle(
                          fontSize: 32.0,
                          fontWeight: FontWeight.normal,
                          decoration: TextDecoration.underline,
                        ),
                      ),
                    ],
                  ),
                )
              ]));
  }

  void _showMarkerListBottomSheet() {
    if (!Get.find<VisibilitySevice>().isBottomSheetOpen) {
      Get.find<VisibilitySevice>().isInfoSheetVisible = false;
      Get.find<VisibilitySevice>().isBottomSheetOpen = true;
      _scaffoldKey.currentState?.showBottomSheet(
        (BuildContext context) {
          return Stack(
            children: [
              Container(
                width: MediaQuery.of(context).size.width,
                height: 330.0,
                padding: EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "Mark your places",
                      style: GoogleFonts.roboto(
                          color: Theme.of(context).colorScheme.onBackground,
                          fontSize: 28,
                          fontWeight: FontWeight.bold),
                    ),
                    SizedBox(height: 16.0),
                    Wrap(
                      spacing: 16.0,
                      runSpacing: 16.0,
                      children: [
                        _buildMarkerOption(
                            "Home",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("Home")]!),
                        _buildMarkerOption(
                            "Work",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("Work")]!),
                        _buildMarkerOption(
                            "School",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("School")]!),
                        _buildMarkerOption(
                            "Shop",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("Shop")]!),
                        _buildMarkerOption(
                            "Gym",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("Gym")]!),
                        _buildMarkerOption(
                            "Favorite",
                            Get.find<LocationService>()
                                .staticImages[MarkerId("Favorite")]!),
                      ],
                    ),
                    SizedBox(height: 16.0),
                  ],
                ),
              ),
              Positioned(
                top: 8.0,
                right: 8.0,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                    Get.find<VisibilitySevice>().isBottomSheetOpen = false;
                  },
                  style: ElevatedButton.styleFrom(
                    shape: CircleBorder(),
                  ),
                  child: Icon(
                    Icons.close,
                    color: Colors.black,
                  ),
                ),
              ),
            ],
          );
        },
      );
    }
  }

  Widget _buildMarkerOption(String markerName, Uint8List iconData) {
    return GestureDetector(
      onTap: () {
        if (Get.find<LocationService>()
                .staticMarkerTypeId
                .containsKey(markerName) &&
            Get.find<LocationService>()
                    .staticMarkerTypeId[markerName]!
                    .length >=
                5) {
          Get.dialog(
            AlertDialog(
              title: Text('Ooooops...'),
              content: Text("You can't add more than 5 ${markerName}s"),
              actions: <Widget>[
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: Text('OK'),
                ),
              ],
            ),
          );
        } else {
          _addStaticMarker(markerName);
          Navigator.of(context).pop();
          Get.find<VisibilitySevice>().isBottomSheetOpen = false;
        }
      },
      child: Container(
        width: 100.0,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 70.0,
              height: 70.0,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: Colors.grey[700]!,
              ),
              child: Center(
                child: Image.memory(
                  iconData,
                  width: 65.0,
                  height: 65.0,
                ),
              ),
            ),
            SizedBox(height: 8.0),
            Text(markerName),
          ],
        ),
      ),
    );
  }

  void _addStaticMarker(String markerType) async {
    GoogleMapController controller = await _mapController.future;
    LatLng center = await controller.getLatLng(ScreenCoordinate(
      x: MediaQuery.of(context).size.width ~/ 2,
      y: MediaQuery.of(context).size.height ~/ 2,
    ));
    MarkerId markerId = MarkerId(markerType);
    String newMarkerName = markerType;

    int i = 1;
    if (Get.find<LocationService>()
        .staticMarkerTypeId
        .containsKey(markerType)) {
      while (Get.find<LocationService>()
          .staticMarkerTypeId[markerType]!
          .contains(newMarkerName)) {
        newMarkerName = "${markerType} ${i.toString()}";
        i++;
      }
    }
    markerId = MarkerId(newMarkerName);
    Marker marker = Marker(
      markerId: markerId,
      position: center,
      draggable: true,
      icon:
          Get.find<LocationService>().staticMarkerIcons[MarkerId(markerType)]!,
      onTap: () {
        setState(() {
          Get.find<VisibilitySevice>().isInfoSheetVisible = true;
          if (Get.find<VisibilitySevice>().isBottomSheetOpen) {
            Navigator.of(context).pop();
            Get.find<VisibilitySevice>().isBottomSheetOpen = false;
          }
          Get.find<VisibilitySevice>().highlightedMarker = markerId;
          Get.find<VisibilitySevice>().highlightedMarkerType = markerType;
        });
      },
      onDragEnd: (LatLng newPosition) {
        // TODO: update the marker's position in the data structure
      },
    );
    setState(() {
      Get.find<LocationService>().addStaticMarker(marker);
      Get.find<LocationService>().updateMarkers();
      if (Get.find<LocationService>()
          .staticMarkerTypeId
          .containsKey(markerType)) {
        Get.find<LocationService>()
            .staticMarkerTypeId[markerType]!
            .add(newMarkerName);
      } else {
        Get.find<LocationService>().staticMarkerTypeId[markerType] = {
          newMarkerName
        };
      }
    });
  }

  @override
  void dispose() {
    _debounce.cancel();
    _markersTimer.cancel();
    _stateTimer.cancel();
    Get.find<MapService>().pausePositionStream();
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }
}

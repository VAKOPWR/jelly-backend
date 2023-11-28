import 'package:google_maps_flutter/google_maps_flutter.dart';

class Friend {
  String id;
  String name;
  LatLng location;
  int batteryPercentage;
  double movementSpeed;
  bool isOnline;
  String offlineStatus;

  Friend(
      {required this.id,
      required this.name,
      required this.location,
      required this.batteryPercentage,
      required this.movementSpeed,
      this.isOnline = false,
      this.offlineStatus = '**'});

  factory Friend.fromJson(Map<String, dynamic> json) {
    return Friend(
      id: json['id'].toString(),
      // name: json['nickname'],
      name: 'TMP',
      location: LatLng(json['positionLat'], json['positionLon']),
      batteryPercentage: json['batteryLevel'],
      movementSpeed: json['speed'],
    );
  }
}
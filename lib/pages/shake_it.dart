import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:project_jelly/classes/basic_user.dart';
import 'package:project_jelly/service/map_service.dart';
import 'package:project_jelly/service/request_service.dart';

class ShakeItScreen extends StatefulWidget {
  ShakeItScreen({Key? key}) : super(key: key);

  @override
  _ShakeItScreenState createState() => _ShakeItScreenState();
}

class _ShakeItScreenState extends State<ShakeItScreen> {
  List<BasicUser> usersWhoAreShaking = [];
  bool isShaking = false;

  @override
  void initState() {
    super.initState();
    _fetchShakingUsers();
  }

  Future<void> _fetchShakingUsers() async {
    List<BasicUser> _usersWhoAreShaking = await Get.find<RequestService>()
        .getFriendsBasedOnEndpoint('/user/nearby');

    setState(() {
      usersWhoAreShaking = _usersWhoAreShaking;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          "Shake IT",
          style: TextStyle(fontSize: 24.0),
        ),
        centerTitle: true,
        toolbarHeight: 80.0,
        backgroundColor: Colors.green,
        elevation: 0,
        titleSpacing: 16.0,
      ),
      body: Container(
        color: Colors.green,
        child: Column(
          children: [
            Expanded(
              child: Center(
                child: Container(
                  padding: EdgeInsets.symmetric(vertical: 16.0),
                  margin: EdgeInsets.all(24.0),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(24.0),
                  ),
                  child: Column(
                    children: [
                      Text(
                        isShaking
                            ? "Shaking! Who else is?"
                            : "Also shaking near you",
                        style: TextStyle(
                          fontSize: 24.0,
                          fontWeight: FontWeight.bold,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      Expanded(
                        child: ListView.builder(
                          shrinkWrap: true,
                          itemCount: usersWhoAreShaking.length,
                          itemBuilder: (BuildContext context, int index) {
                            return _buildFriendRow(usersWhoAreShaking[index]);
                          },
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Icon(
              Icons.phone_android,
              size: 160.0,
              color: Colors.white,
            ),
            SizedBox(height: 100),
          ],
        ),
      ),
    );
  }

  Widget _buildFriendRow(BasicUser userWhoIsShaking) {
    return ListTile(
      leading: CircleAvatar(
        backgroundImage: Get.find<MapService>().imageProviders[userWhoIsShaking.id],
        radius: 29,
      ),
      title: Text(
        userWhoIsShaking.name,
        style: TextStyle(color: Colors.black),
      ),
      onTap: () {
      },
    );
  }
}
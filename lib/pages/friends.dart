import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../classes/Friend.dart';
import '../widgets/SearchBar.dart';

const int _numberOfTabs = 3;

class FriendsPage extends StatefulWidget {
  const FriendsPage({super.key});

  @override
  State<FriendsPage> createState() => _FriendsPageState();
}

class _FriendsPageState extends State<FriendsPage>
    with SingleTickerProviderStateMixin {
  final _biggerFont = const TextStyle(fontSize: 18.0);
  List<Friend> _listFriends = [];
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _numberOfTabs, vsync: this);
    _tabController.addListener(() {
      setState(() {});
    });
    _fetchFriendsList();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  void _handleTabChange(int newIndex) {
    setState(() {
      _tabController.index = newIndex;
    });
  }

  void _handleShakeButtonPressed() {
    setState(() {
      // Handle the "SHAKE IT" button action here
    });
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: _numberOfTabs,
      child: Scaffold(
        appBar: _buildAppBar(),
        body: TabBarView(
          controller: _tabController,
          children: [
            FriendListTab(
              friends: _listFriends,
              onTabChange: _handleTabChange,
              buildRowForFriendList: _buildRowForFriendList,
            ),
            FriendFindingTab(
              friends: _listFriends,
              onTabChange: _handleTabChange,
              onShakeButtonPressed: _handleShakeButtonPressed,
              buildRowForFriendFinding: _buildRowForFriendFinding,
            ),
            FriendPendingTab(
              friends: _listFriends,
              onTabChange: _handleTabChange,
              buildRowForFriendPending: _buildRowForFriendPending,
            ),
          ],
        ),
      ),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      title: const Text("Friends"),
      centerTitle: true,
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () {
          Navigator.pushReplacementNamed(context, '/map');
        },
      ),
      bottom: TabBar(
        controller: _tabController,
        tabs: _buildTabsWithBadges(),
      ),
    );
  }

  List<Widget> _buildTabsWithBadges() {
    return [
      const Tab(text: "List"),
      const Tab(text: "Find"),
      Tab(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            const Text("Pending"),
            Container(
              margin: const EdgeInsets.only(left: 4.0),
              padding: const EdgeInsets.all(4.0),
              decoration: const BoxDecoration(
                shape: BoxShape.circle,
                color: Colors.red,
              ),
              child: Text(
                _listFriends.length.toString(),
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
      ),
    ];
  }

  Widget _buildRowForFriendList(Friend friend) {
    return ListTile(
      leading: const CircleAvatar(
        backgroundColor: Colors.grey,
        backgroundImage: NetworkImage(
            'https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50'),
      ),
      title: Text(
        friend.name,
        style: _biggerFont,
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.more_vert),
            onPressed: () {},
          ),
        ],
      ),
      onTap: () {
        setState(() {});
      },
    );
  }

  Widget _buildRowForFriendFinding(Friend friend) {
    return ListTile(
      leading: const CircleAvatar(
        backgroundColor: Colors.grey,
        backgroundImage: NetworkImage(
            'https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50'),
      ),
      title: Text(
        friend.name,
        style: _biggerFont,
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.person_add_alt_1),
            onPressed: () {},
          ),
        ],
      ),
      onTap: () {
        setState(() {});
      },
    );
  }

  Widget _buildRowForFriendPending(Friend friend) {
    return ListTile(
      leading: const CircleAvatar(
        backgroundColor: Colors.grey,
        backgroundImage: NetworkImage(
            'https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50'),
      ),
      title: Text(
        friend.name,
        style: _biggerFont,
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.close),
            onPressed: () {},
          ),
        ],
      ),
      onTap: () {
        setState(() {});
      },
    );
  }

  _fetchFriendsList() async {
    var url = 'https://jsonplaceholder.typicode.com/users';
    var httpClient = HttpClient();
    List<Friend> listFriends = [];

    try {
      var request = await httpClient.getUrl(Uri.parse(url));
      var response = await request.close();
      if (response.statusCode == HttpStatus.ok) {
        var json = await utf8.decoder.bind(response).join();
        List<dynamic> data = jsonDecode(json);

        for (var res in data) {
          var objName = res['name'];
          String name = objName.toString();

          var objLat = res['address']['geo']['lat'];
          double latitude;
          if (objLat is String) {
            latitude = double.tryParse(objLat) ?? 0.0;
          } else {
            latitude = objLat ?? 0.0;
          }

          var objLng = res['address']['geo']['lng'];
          double longitude;
          if (objLng is String) {
            longitude = double.tryParse(objLng) ?? 0.0;
          } else {
            longitude = objLng ?? 0.0;
          }

          String avatar =
              'https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50';

          Friend friendModel =
              Friend(name, avatar, LatLng(latitude, longitude));
          listFriends.add(friendModel);
        }
      }
    } catch (exception) {
      print(exception.toString());
    }

    if (!mounted) return;

    setState(() {
      _listFriends = listFriends;
    });
  }
}

class FriendListTab extends StatelessWidget {
  final List<Friend> friends;
  final void Function(int) onTabChange;
  final Widget Function(Friend) buildRowForFriendList;

  const FriendListTab({super.key,
    required this.friends,
    required this.onTabChange,
    required this.buildRowForFriendList,
  });

  @override
  Widget build(BuildContext context) {
    return SearchBarWidget(
      content: ListView.builder(
        itemCount: friends.length * 2,
        itemBuilder: (context, i) {
          if (i.isOdd) return const Divider();
          final friendIndex = i ~/ 2;
          if (friendIndex < friends.length) {
            return buildRowForFriendList(friends[friendIndex]);
          }
          return null;
        },
      ),
    );
  }
}

class FriendFindingTab extends StatelessWidget {
  final List<Friend> friends;
  final void Function(int) onTabChange;
  final VoidCallback? onShakeButtonPressed;
  final Widget Function(Friend) buildRowForFriendFinding;

  const FriendFindingTab({
    Key? key,
    required this.friends,
    required this.onTabChange,
    this.onShakeButtonPressed,
    required this.buildRowForFriendFinding,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: SearchBarWidget(
            content: ListView.builder(
              itemCount: friends.length * 2,
              itemBuilder: (context, i) {
                if (i.isOdd) return const Divider();
                final friendIndex = i ~/ 2;
                if (friendIndex < friends.length) {
                  return buildRowForFriendFinding(friends[friendIndex]);
                }
                return null;
              },
            ),
          ),
        ),
        const SizedBox(
          height: 90.0,
          child: Center(
            child: Text(
              "OR",
              style: TextStyle(fontSize: 24.0),
            ),
          ),
        ),
        SizedBox(
          width: double.infinity,
          height: 80.0,
          child: ElevatedButton(
            onPressed: onShakeButtonPressed,
            child: const Text(
              "SHAKE IT",
              style: TextStyle(fontSize: 42.0),
            ),
          ),
        ),
      ],
    );
  }
}

class FriendPendingTab extends StatelessWidget {
  final List<Friend> friends;
  final void Function(int) onTabChange;
  final Widget Function(Friend) buildRowForFriendPending;

  const FriendPendingTab({super.key,
    required this.friends,
    required this.onTabChange,
    required this.buildRowForFriendPending,
  });

  @override
  Widget build(BuildContext context) {
    return SearchBarWidget(
      content: ListView.builder(
        itemCount: friends.length * 2,
        itemBuilder: (context, i) {
          if (i.isOdd) return const Divider();
          final friendIndex = i ~/ 2;
          if (friendIndex < friends.length) {
            return buildRowForFriendPending(friends[friendIndex]);
          }
          return null;
        },
      ),
    );
  }
}
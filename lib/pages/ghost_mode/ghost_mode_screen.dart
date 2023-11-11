import 'package:flutter/material.dart';
import 'package:project_jelly/pages/ghost_mode/tabs/ghost_mode_tab_everyone.dart';
import 'package:project_jelly/pages/ghost_mode/tabs/ghost_mode_tab_friends.dart';
import 'package:project_jelly/pages/ghost_mode/tabs/ghost_mode_tab_groups.dart';

class GhostMode extends StatefulWidget {
  const GhostMode({Key? key}) : super(key: key);

  @override
  State<GhostMode> createState() => _GhostModeState();
}

class _GhostModeState extends State<GhostMode> {
  Widget build(BuildContext context) {
    return DefaultTabController(
        length: 3,
        child: Scaffold(
            appBar: AppBar(
              title: Text('Ghost Mode'),
              centerTitle: true,
              elevation: 0.0,
            ),
            body: Container(
              child: const Column(
                children: [
                  TabBar(
                    tabs: [
                      Tab(
                        text: 'Everyone',
                      ),
                      Tab(
                        text: 'Groups',
                      ),
                      Tab(
                        text: 'Friends',
                      )
                    ],
                  ),
                  Expanded(
                      child: TabBarView(
                    children: [
                      GhostModeTabEveryone(),
                      GhostModeTabGroups(),
                      GhostModeTabFriends()
                    ],
                  ))
                ],
              ),
            )));
  }
}
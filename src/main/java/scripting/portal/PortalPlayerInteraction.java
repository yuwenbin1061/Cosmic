/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting.portal;

import client.MapleClient;
import scripting.AbstractPlayerInteraction;
import scripting.map.MapScriptManager;
import server.maps.MaplePortal;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    private final MaplePortal portal;

    public PortalPlayerInteraction(MapleClient c, MaplePortal portal) {
        super(c);
        this.portal = portal;
    }

    public MaplePortal getPortal() {
        return portal;
    }

    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(c, "onUserEnter/" + portal.getScriptName(), false);
    }

    public boolean hasLevel30Character() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `level` FROM `characters` WHERE accountid = ?")) {
            ps.setInt(1, getPlayer().getAccountID());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("level") >= 30) {
                        return true;
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return getPlayer().getLevel() >= 30;
    }

    public void blockPortal() {
        c.getPlayer().blockPortal(getPortal().getScriptName());
    }

    public void unblockPortal() {
        c.getPlayer().unblockPortal(getPortal().getScriptName());
    }

    public void playPortalSound() {
        c.announce(MaplePacketCreator.playPortalSound());
    }
}
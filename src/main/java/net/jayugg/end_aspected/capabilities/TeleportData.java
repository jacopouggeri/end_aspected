package net.jayugg.end_aspected.capabilities;

public class TeleportData {
    private static final int TELEPORT_CYCLE = 1000;
    private int teleportCount = 0;

    public void incrementTeleportCount() {
        this.teleportCount++;
        this.teleportCount %= TELEPORT_CYCLE;
    }

    public int getTeleportCount() {
        return this.teleportCount;
    }

    public void setTeleportCount(int teleportCount) {
        this.teleportCount = teleportCount % TELEPORT_CYCLE;
    }

    public int getSubCycleValue() {
        int subCycleCount = this.teleportCount / 100;
        int subCyle = this.teleportCount % 100;
        if (subCyle == 0) {
            if (subCycleCount % 5 == 0) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
}


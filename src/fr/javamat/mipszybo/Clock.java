package fr.javamat.mipszybo;

import java.util.ArrayList;

public class Clock extends Thread {
	private ArrayList<Sync> syncListener;

	public Clock() {
		syncListener = new ArrayList<Sync>();
	}

	@Override
	public void run() {
		while (true) {
			for (Sync sync : syncListener) {
				sync.tick();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addSyncListener(Sync listener) {
		syncListener.add(listener);
	}

	public void removeSyncListener(Sync listener) {
		syncListener.remove(listener);
	}
}

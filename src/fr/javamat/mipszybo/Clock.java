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
			try {
				Thread.sleep(10);
				for (Sync sync : syncListener) {
					sync.tick();
				}
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

package gameEngine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class World {
	/**
	 * Struct to store state of the "world" Walls, nibbles and global time
	 */
	public int height, width;
	public long clock;
	public int maxNibbles = 20;
	private Semaphore nibbleProtect = new Semaphore(1); // protect nibble list
														// add/remove with
														// semaphore
	private LinkedList<PhysicalCircle> nibbles = new LinkedList<PhysicalCircle>();

	public void newNibble(int n) {
		try {
			nibbleProtect.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < n; i++) {
			if (nibbles.size() >= maxNibbles)
				break;
			PhysicalCircle nibble = new PhysicalCircle(0, 0, GameLoop.globalCircleRadius);
			nibble.x = Math.random() * (width - 2 * nibble.rad) + nibble.rad;
			nibble.y = Math.random() * (height - 2 * nibble.rad) + nibble.rad;

			nibble.vx = 2 * (Math.random() - .5);
			nibble.vy = 2 * (Math.random() - .5);
			nibble.t = 0;
			nibbles.add(nibble);
		}
		nibbleProtect.release();
	}

	public LinkedList<PhysicalCircle> getNibbles() {
		return nibbles;
	}

	public int calcValue(PhysicalCircle p) {
		return (int) (5 + (8d * Math.min(Math.exp(-(double) (p.t - 800) / 2000d), 1)));
	}

	public void update(int w, int h) {
		this.width = w;
		this.height = h;
		for (PhysicalCircle p : nibbles) {
			p.updatePosition();
			p.collideWall(50, 50, w - 50, h - 50);
		}
		clock += GameLoop.UPDATEPERIOD;
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		for (PhysicalCircle nibble : nibbles) {
			g.fillOval((int) (nibble.x - nibble.rad), (int) (nibble.y - nibble.rad), (int) (2 * nibble.rad + 1), (int) (2 * nibble.rad + 1));
		}
	}

	public void removeNibbles(LinkedList<PhysicalCircle> rem) {
		try {
			nibbleProtect.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (PhysicalCircle p : rem) {
			nibbles.remove(p);
		}
		nibbleProtect.release();
	}

	public void reset() {
		try {
			nibbleProtect.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nibbles.clear();
		nibbleProtect.release();
		clock = 0;
	}
}

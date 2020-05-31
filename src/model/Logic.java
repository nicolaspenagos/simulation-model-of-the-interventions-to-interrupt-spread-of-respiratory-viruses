/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* @author Nicol�s Penagos Montoya
* nicolas.penagosm98@gmail.com
**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

package model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Random;

import customThreads.MovementThread;

public class Logic{
	
	// -------------------------------------
	// Constants
	// -------------------------------------
	public final static double LEFT_LIMIT = 398.0;
	public final static double RIGHT_LIMIT = 1185.0;
	public final static double UP_LIMIT = 166.0;
	public final static double DOWN_LIMIT = 690.0;
	
	public final static double MAX_VEL = 4.0;
	public final static double TOLERANCE = 4;
	
	public final static double IE_FREQUENT_HANDWAHING = 0.55;
	public final static double IE_WEARING_MASK = 0.68;
	public final static double IE_WEARING_N95_MASK = 0.91;
	public final static double IE_WEARING_GLOVES = 0.57;
	public final static double IE_WEARING_GOWN = 0.77;
	public final static double IE_WEARING_ALL = 0.91;
	
	// -------------------------------------
	// Atributtes and relationships
	// -------------------------------------
	private int infectedPeople;
	private int recoveredPeople;
	private int healthyPeople;
	private int totalPeople;
	private int radius;
	private boolean pause;
	private boolean deleteAllPeople;
	private boolean frequendHandwashing;
	private boolean wearingMask;
	private boolean wearingN95Mask;
	private boolean wearingGloves;
	private boolean wearingGown;
	private boolean wearingAll;
	
	private List<ModelCircle> people;
	private MovementThread movementThread;
	
	// -------------------------------------
	// Constructor
	// -------------------------------------
	public Logic() {
		
		people = new ArrayList<ModelCircle>();
		deleteAllPeople = false;
		
	}

	// -------------------------------------
	// Methods
	// -------------------------------------
	public int getTotalPeople() {
		return infectedPeople + recoveredPeople + healthyPeople;
	}
	
	public void loadPeople() {
		
		people = new ArrayList<ModelCircle>();
		totalPeople = infectedPeople + recoveredPeople + healthyPeople;
		Random r = new Random();
		
		if(totalPeople < 500) {
			radius = 3;
		}else if(totalPeople < 1500) {
			radius = 2;
		}else {
			radius = 1;
		}
			
		for (int i = 0; i < totalPeople; i++) {
			
			int ramdonX = ThreadLocalRandom.current().nextInt((int)LEFT_LIMIT, (int)RIGHT_LIMIT + 1);
			int ramdonY = ThreadLocalRandom.current().nextInt((int)UP_LIMIT, (int)DOWN_LIMIT + 1);
			int velX    = ThreadLocalRandom.current().nextInt(1,(int)MAX_VEL +1 );
			int velY    = ThreadLocalRandom.current().nextInt(1,(int)MAX_VEL +1 );
			char condition;
			
			if(r.nextBoolean()) {
				velX*=-1;
			}
			
			if(r.nextBoolean()) {
				velY*=-1;
			}
			
			if(i < infectedPeople) {
				condition = Person.INFECTED;	
			}else if(i >= infectedPeople && i < infectedPeople + recoveredPeople) {
				condition = Person.RECOVERED;
			}else {
				condition = Person.HEALTHY;	
			}
			
			people.add(new ModelCircle(condition, ramdonX, ramdonY, velX, velY, radius));
			
		}
		
	}
	
	public void move() {
		
		if(!pause) {
			
			for (int i = 0; i < people.size(); i++) {
				
				ModelCircle current = people.get(i);
				current.move();
				contact(current, i);
				
			}
			//System.out.println("");
			if(deleteAllPeople) {
				
				people = new ArrayList<ModelCircle>();
				deleteAllPeople = false;
				
			}
		
		}
		
	}
	
	public void killMovThread() {
		movementThread.kill();
	}
	
	public void contact(ModelCircle current, int index) {
	
		for (int i = 0; i < people.size(); i++) {
			
			if(people.size()!=0) {
				if(i!=index) {
					
					ModelCircle other = people.get(i);
					double dist = Math.sqrt( (current.getPosX()-other.getPosX())*(current.getPosX()-other.getPosX())  + (current.getPosY()-other.getPosY())*(current.getPosY()-other.getPosY()) );
					
					if(dist<=current.getRadius()+other.getRadius()) {
			
						other.bounce();
						current.bounce();	
						
						if(other.getHealthCondition() == Person.INFECTED && current.getHealthCondition() == Person.HEALTHY) {
							
							current.setHealthCondition(Person.INFECTED);
							infectedPeople++;
							healthyPeople--;
							
							
						}else if(current.getHealthCondition() == Person.INFECTED && other.getHealthCondition() == Person.HEALTHY) {
							
							other.setHealthCondition(Person.INFECTED);
							infectedPeople++;
							healthyPeople--;
							
						}
					}
				}
			}
		}
	}
	
	public void startMovementThread() {
		
		movementThread = new MovementThread(this);
		movementThread.setDaemon(true);
		movementThread.start();
		
	}
	
	
	// -------------------------------------
	// Getters and Setters
	// -------------------------------------
	public int getInfectedPeople() {
		return infectedPeople;
	}

	public void setInfectedPeople(int infectedPeople) {
		this.infectedPeople = infectedPeople;
	}

	public int getRecoveredPeople() {
		return recoveredPeople;
	}

	public void setRecoveredPeople(int recoveredPeople) {
		this.recoveredPeople = recoveredPeople;
	}

	public int getHealthyPeople() {
		return healthyPeople;
	}

	public void setHealthyPeople(int healthyPeople) {
		this.healthyPeople = healthyPeople;
	}

	public List<ModelCircle> getPeople() {
		return people;
	}

	public void setPeople(List<ModelCircle> people) {
		this.people = people;
	}

	public boolean isDeleteAllPeople() {
		return deleteAllPeople;
	}

	public void setDeleteAllPeople(boolean deleteAllPeople) {
		this.deleteAllPeople = deleteAllPeople;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isFrequendHandwashing() {
		return frequendHandwashing;
	}

	public void setFrequendHandwashing(boolean frequendHandwashing) {
		this.frequendHandwashing = frequendHandwashing;
	}

	public boolean isWearingMask() {
		return wearingMask;
	}

	public void setWearingMask(boolean wearingMask) {
		this.wearingMask = wearingMask;
	}

	public boolean isWearingN95Mask() {
		return wearingN95Mask;
	}

	public void setWearingN95Mask(boolean wearingN95Mask) {
		this.wearingN95Mask = wearingN95Mask;
	}

	public boolean isWearingGloves() {
		return wearingGloves;
	}

	public void setWearingGloves(boolean wearingGloves) {
		this.wearingGloves = wearingGloves;
	}

	public boolean isWearingGown() {
		return wearingGown;
	}

	public void setWearingGown(boolean wearingGown) {
		this.wearingGown = wearingGown;
	}

	public boolean isWearingAll() {
		return wearingAll;
	}

	public void setWearingAll(boolean wearingAll) {
		this.wearingAll = wearingAll;
	}

}

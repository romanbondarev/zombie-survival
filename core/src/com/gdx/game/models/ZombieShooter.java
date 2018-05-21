package com.gdx.game.models;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.items.weapons.Bullet;
import com.gdx.game.models.animations.ZombieShooterAnimation;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;
import com.gdx.game.utils.Constants;

import static com.gdx.game.utils.Constants.PPM;

public final class ZombieShooter extends Enemy {
    private int counter = 0;
    private int random = 0;
    private double distanceInBetween;
    private ZombieShooterAnimation zombieShooterAnimation;

    public ZombieShooter(GameState gameState, float x, float y) {
        super(gameState, x, y);
        zombieShooterAnimation = new ZombieShooterAnimation(this);
    }

    @Override
    public void update(Player player) {
        double x = player.getPosition().x - this.getPosition().x;
        double y = player.getPosition().y - this.getPosition().y;
        double degrees = -Math.toDegrees(Math.atan2(x, y)) + 90;
        distanceInBetween = Math.sqrt(x * x + y * y);
        zombieShooterAnimation.updateAnimation(degrees);

        if (distanceInBetween < 3) {
            attackPlayer(player);
            zombieShooterAnimation.setAnimation(ZombieShooterAnimation.AnimationType.IDLE);
        } else if (distanceInBetween < 10) {
            body.setLinearVelocity((float) (x / distanceInBetween) * 5, (float) (y / distanceInBetween) * 5);
            if (distanceInBetween > 3.15) zombieShooterAnimation.setAnimation(ZombieShooterAnimation.AnimationType.MOVE);
            if (trouble) {
                if (one) {
                    body.setLinearVelocity(4f, -4f);
                    xPos.add(this.getPosition().x);
                    System.out.println(xPos.size());
                    if (xPos.size() > 100 && xPos.get(xPos.size() - 1).equals(xPos.get(xPos.size() - 1 - 50))) {
                        trouble = false;
                    }
                }
                if (two) {
                    body.setLinearVelocity(-5f, -5f);
                    yPos.add(this.getPosition().y);
                    if (yPos.size() > 100 && yPos.get(yPos.size() - 1).equals(yPos.get(yPos.size() - 1 - 50))) {
                        trouble = false;
                    }
                }
                if (three) {
                    body.setLinearVelocity(4f, 4f);
                    yPos.add(this.getPosition().y);
                    System.out.println(yPos.size());
                    if (yPos.size() > 100 && yPos.get(yPos.size() - 1).equals(yPos.get(yPos.size() - 1 - 50))) {
                        trouble = false;
                    }
                }
                if (four) {
                    body.setLinearVelocity(-4f, 6f);
                    xPos.add(this.getPosition().x);
                    if (xPos.size() > 100 && xPos.get(xPos.size() - 1).equals(xPos.get(xPos.size() - 1 - 50))) {
                        trouble = false;
                    }
                }
            }
            if (xPos.size() > 0 && one) {
                if (xPos.get(0) + 1 < xPos.get(xPos.size() - 1) && xPos.size() > 70) {
                    body.setLinearVelocity((float) (x / distanceInBetween) * 5, (float) (y / distanceInBetween) * 5);
                    trouble = false;
                    one = false;
                    xPos.clear();
                    yPos.clear();
                }
            } else if (xPos.size() > 0 && four) {
                if (xPos.get(0) - 1 > xPos.get(xPos.size() - 1) && xPos.size() > 70) {
                    body.setLinearVelocity((float) (x / distanceInBetween) * 5, (float) (y / distanceInBetween) * 5);
                    yPos.clear();
                    trouble = false;
                    four = false;
                    xPos.clear();
                    yPos.clear();
                }
            } else if (yPos.size() > 0 && two) {
                if (yPos.get(0) > yPos.get(yPos.size() - 1) + 1 && yPos.size() > 70) {
                    body.setLinearVelocity((float) (x / distanceInBetween) * 5, (float) (y / distanceInBetween) * 5);
                    trouble = false;
                    xPos.clear();
                    two = false;
                    yPos.clear();
                }
            } else if (yPos.size() > 0 && three) {
                if (yPos.get(0) < yPos.get(yPos.size() - 1) - 1 && yPos.size() > 70) {
                    body.setLinearVelocity((float) (x / distanceInBetween) * 5, (float) (y / distanceInBetween) * 5);
                    yPos.clear();
                    three = false;
                    trouble = false;
                    xPos.clear();
                }
            }

            if (distanceInBetween < 20 && !trouble) {
                if (xPos.size() == 150) {
                    xPos.clear();
                    yPos.clear();
                }
                xPos.add(this.getPosition().x);
                yPos.add(this.getPosition().y);
                if (xPos.get(0).equals(xPos.get(xPos.size() - 1)) && degrees < 20 && degrees > -20 && xPos.size() > 20) {
                    trouble = true;
                    one = true;
                    xPos.clear();
                    yPos.clear();
                } else if (xPos.get(0).equals(xPos.get(xPos.size() - 1)) && degrees < 225 && degrees > 180 && xPos.size() > 20) {
                    trouble = true;
                    four = true;
                    xPos.clear();
                    yPos.clear();
                } else if (yPos.get(0).equals(yPos.get(yPos.size() - 1)) && (degrees < -65 || degrees > 245) && yPos.size() > 20) {
                    trouble = true;
                    two = true;
                    xPos.clear();
                    yPos.clear();
                } else if (yPos.get(0).equals(yPos.get(yPos.size() - 1)) && degrees > 65 && degrees < 115 && yPos.size() > 20) {
                    trouble = true;
                    three = true;
                    xPos.clear();
                    yPos.clear();
                }
                body.setLinearVelocity((float) (x / distanceInBetween) * 5 * multiplier, (float) (y / distanceInBetween) * 5 * multiplier);
            } else {
                zombieShooterAnimation.setAnimation(ZombieShooterAnimation.AnimationType.IDLE);
            }
        }
    }

    private void attackPlayer(Player player) {
        double x = player.getPosition().x - this.getPosition().x;
        double y = player.getPosition().y - this.getPosition().y;
        distanceInBetween = Math.sqrt(x * x + y * y);
        counter++;

        boolean canShoot = true;
        for (Zombie zombie : ((PlayState) gameState).getZombies()) {
            if (Intersector.intersectSegmentCircle(new Vector2(getPosition().x, getPosition().y), new Vector2(player.getPosition().x, player.getPosition().y), new Vector2(zombie.getPosition().x, zombie.getPosition().y), 25 / PPM)) {
                canShoot = false;
                break;
            }
        }
        if (canShoot && counter >= 20 + random) {
            ((PlayState) gameState).getBullets().add(new Bullet(world, new Vector2(getPosition().x, getPosition().y), new Vector2(player.getPosition().x,
                    player.getPosition().y), Constants.shooterDamage));
            random = r.nextInt(30);
            counter = 0;
        }
    }

    public ZombieShooterAnimation getZombieShooterAnimation() {
        return zombieShooterAnimation;
    }
}

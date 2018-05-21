package com.gdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gdx.game.items.weapons.Bullet;
import com.gdx.game.models.Player;
import com.gdx.game.models.Zombie;
import com.gdx.game.models.ZombieShooter;

public class GameContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        /* Collision detection between Player and Bullet */
        bulletPlayerHandler(fa, fb);
        bulletPlayerHandler(fb, fa);

        /* Collision detection between ZombieShooter and Bullet */
        bulletShooterHandler(fa, fb);
        bulletShooterHandler(fb, fa);

        /* Collision detection between Zombie and Bullet */
        zombieBulletHandler(fa, fb);
        zombieBulletHandler(fb, fa);

        /* Collision detection between Bullet and other objects */
        if (fa.getUserData() instanceof Bullet) {
            Bullet bullet = ((Bullet) fa.getUserData());
            bullet.setCanDelete(true);
            fa.getBody().setUserData(bullet);
        }
        if (fb.getUserData() instanceof Bullet) {
            Bullet bullet = ((Bullet) fb.getUserData());
            bullet.setCanDelete(true);
            fb.getBody().setUserData(bullet);
        }
    }

    private void zombieBulletHandler(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof Zombie && fb.getUserData() instanceof Bullet) {
            Zombie zombie = ((Zombie) fa.getUserData());
            Bullet bullet = ((Bullet) fb.getUserData());
            zombie.takeDamage(bullet.getDamage());
            fa.setUserData(zombie);
        }
    }

    private void bulletShooterHandler(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof ZombieShooter && fb.getUserData() instanceof Bullet) {
            ZombieShooter zombie = ((ZombieShooter) fa.getUserData());
            Bullet bullet = ((Bullet) fb.getUserData());
            zombie.takeDamage(bullet.getDamage());
            fa.setUserData(zombie);
        }
    }

    private void bulletPlayerHandler(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof Player && fb.getUserData() instanceof Bullet) {
            Player player = ((Player) fa.getUserData());
            Bullet bullet = ((Bullet) fb.getUserData());
            player.takeDamage(bullet.getDamage());
            fa.setUserData(player);
        }
    }
}

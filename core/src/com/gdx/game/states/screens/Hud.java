package com.gdx.game.states.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.game.Application;
import com.gdx.game.items.Inventory;
import com.gdx.game.items.Item;
import com.gdx.game.models.Player;
import com.gdx.game.states.GameState;
import com.gdx.game.states.PlayState;

import java.util.LinkedList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.gdx.game.utils.Constants.INVENTORY_SIZE;
import static com.gdx.game.utils.Constants.SCALE;
import static com.gdx.game.utils.WCC.worldToPixels;

public class Hud implements Screen {
    private Application application;
    private Player player;
    private Stage stage;
    private Inventory inventory;
    private Skin skin;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private boolean hidden = true;

    private LinkedList<HudInventoryImage> invList = new LinkedList<>();
    private LinkedList<HudInventoryImage> trayList = new LinkedList<>();

    private HorizontalGroup inventoryHGroup;
    private HorizontalGroup trayHGroup;
    private HudInventoryImage helmet;
    private HudInventoryImage vest;
    private HudInventoryImage selector;
    private HudInventoryImage ammoBackground;
    private Label ammoLabel;
    private Label vestAbosrbLabel;
    private Label helmetAbsorbLabel;

    private Texture backgroundTex = Application.assetManager.get("ui/background/background.png", Texture.class);
    private Texture backgroundLargeTex = Application.assetManager.get("ui/background/background-large.png", Texture.class);
    private Texture selectorTex = Application.assetManager.get("ui/inventory-items/selector.png", Texture.class);
    private Texture hideTex = Application.assetManager.get("ui/buttons/hide-button.png", Texture.class);
    private Texture showTex = Application.assetManager.get("ui/buttons/show-button.png", Texture.class);

    private HudInventoryImage toggleInventoryButton;
    private GameState state;

    public Hud(Application application, GameState state, Player player) {
        this.application = application;
        this.state = state;
        this.player = player;
        this.stage = new Stage(new ScreenViewport());
        this.inventory = player.getInventory();
        this.application = application;
        Gdx.input.setInputProcessor(stage);

        /* Setting up a skin for UI widgets */
        skin = new Skin();
        skin.add("default-font", Application.assetManager.get("SegoeUI-Black.ttf", BitmapFont.class), BitmapFont.class);
        skin.load(Gdx.files.internal("ui/uiSkin.json"));

        /* Initializing the inventory UI(HUD) */
        initButtons();
    }

    /**
     * HUD initialization.
     */
    private void initButtons() {
        /* Inventory horizontal group */
        inventoryHGroup = new HorizontalGroup();
        inventoryHGroup.space(10);

        /* Tray horizontal group */
        trayHGroup = new HorizontalGroup();
        trayHGroup.space(10);

        ClickListener clickListener = new ClickListener(Input.Buttons.RIGHT) {
            /* Mouse RIGHT CLICK listener */
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int slotID = ((HudInventoryImage) event.getTarget()).getId();
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    if (slotID == -2) {
                        /* Clicked on helmet */
                        Item itemToRemove = inventory.getHelmetArmor();
                        if (itemToRemove != null) {
                            itemToRemove.getCircleSprite().setPosition(worldToPixels(player.getPosition().x) - itemToRemove.getCircleSprite().getWidth() / 2,
                                    worldToPixels(player.getPosition().y) - itemToRemove.getCircleSprite().getHeight() / 2);
                            ((PlayState) state).getItems().add(itemToRemove);
                            inventory.setHelmetArmor(null);
                        }
                    } else if (slotID == -3) {
                        /* Clicked on vest */
                        Item itemToRemove = inventory.getVestArmor();
                        if (itemToRemove != null) {
                            itemToRemove.getCircleSprite().setPosition(worldToPixels(player.getPosition().x) - itemToRemove.getCircleSprite().getWidth() / 2,
                                    worldToPixels(player.getPosition().y) - itemToRemove.getCircleSprite().getHeight() / 2);
                            ((PlayState) state).getItems().add(itemToRemove);
                            inventory.setVestArmor(null);
                        }
                    } else if (slotID < INVENTORY_SIZE) {
                        /* Clicked on item in INVENTORY */
                        Item itemToRemove = inventory.getItem(slotID);
                        if (itemToRemove != null) {
                            itemToRemove.getCircleSprite().setPosition(worldToPixels(player.getPosition().x) - itemToRemove.getCircleSprite().getWidth() / 2,
                                    worldToPixels(player.getPosition().y) - itemToRemove.getCircleSprite().getHeight() / 2);
                            ((PlayState) state).getItems().add(itemToRemove);
                            inventory.removeItemFromInventory(itemToRemove);
                        }
                    } else if (slotID >= INVENTORY_SIZE) {
                        /* Clicked on item in TRAY */
                        Item itemToRemove = inventory.getTray()[slotID - INVENTORY_SIZE];
                        if (itemToRemove != null) {
                            itemToRemove.getCircleSprite().setPosition(worldToPixels(player.getPosition().x) - itemToRemove.getCircleSprite().getWidth() / 2,
                                    worldToPixels(player.getPosition().y) - itemToRemove.getCircleSprite().getHeight() / 2);
                            ((PlayState) state).getItems().add(itemToRemove);
                            inventory.removeItemFromTray(itemToRemove);
                        }
                    }
                    return;
                }

                if (slotID == -1) {
                    toggleInventory();
                } else if (slotID == -2) {
                    /* Clicked on helmet */
                    inventory.returnItemToInventory(inventory.getHelmetArmor());
                } else if (slotID == -3) {
                    /* Clicked on vest */
                    inventory.returnItemToInventory(inventory.getVestArmor());
                } else if (slotID < INVENTORY_SIZE) {
                    /* Clicked on item in INVENTORY */
                    inventory.selectItem(inventory.getItem(slotID));
                } else if (slotID >= INVENTORY_SIZE) {
                    /* Clicked on item in TRAY */
                    inventory.returnItemToInventory(inventory.getTray()[slotID - INVENTORY_SIZE]);
                }
                event.getTarget().addAction(sequence(alpha(0.5f), alpha(1, 0.05f))); // Click Animation
            }
        };

        /* Setting the position of inventory and tray group */
        inventoryHGroup.setPosition(stage.getWidth() - 33, stage.getHeight() - 33);
        trayHGroup.setPosition(stage.getWidth() - 4 * backgroundTex.getWidth() - 50, 106);

        /* Toggle inventory button, must be created before others to be in front of them */
        toggleInventoryButton = new HudInventoryImage(hideTex, -1);
        toggleInventoryButton.addListener(clickListener);
        inventoryHGroup.addActor(toggleInventoryButton);

        /* Creates a inventory tile and adds it into horizontal inventory group */
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            Item item = inventory.getItem(i);
            HudInventoryImage image = new HudInventoryImage(pickTexture(item), i);
            image.addListener(clickListener);
            invList.add(image);
            inventoryHGroup.addActor(image);
        }

        /* Creates a tray tile and adds it into horizontal inventory group */
        for (int i = 0; i < 4; i++) {
            Item item = inventory.getTray()[i];
            HudInventoryImage image = new HudInventoryImage(pickTexture(item), INVENTORY_SIZE + i);
            image.addListener(clickListener);
            trayList.add(image);
            trayHGroup.addActor(image);
        }

        /* Helmet armor tile */
        helmet = new HudInventoryImage(pickTexture(null), -2);
        helmet.setPosition(stage.getWidth() - backgroundTex.getWidth() - 20, 198);
        helmet.addListener(clickListener);

        /* Bulletproof vest armor tile */
        vest = new HudInventoryImage(pickTexture(null), -3);
        vest.setPosition(stage.getWidth() - backgroundTex.getWidth() - 20, 142);
        vest.addListener(clickListener);

        /* Helmet damage absorption label */
        helmetAbsorbLabel = new Label("0", skin);
        helmetAbsorbLabel.setFontScale(0.5f);
        helmetAbsorbLabel.setPosition(stage.getWidth() - 15, 200);

        /* Bulletproof damage absorption label */
        vestAbosrbLabel = new Label("0", skin);
        vestAbosrbLabel.setFontScale(0.5f);
        vestAbosrbLabel.setPosition(stage.getWidth() - 15, 145);

        /* Ammo counter's background */
        ammoBackground = new HudInventoryImage(backgroundLargeTex, -1);
        ammoBackground.setPosition(stage.getWidth() - backgroundLargeTex.getWidth() - 20, 20);

        /* Ammo counter's text */
        ammoLabel = new Label("", skin);
        ammoLabel.setBounds(stage.getWidth() - backgroundLargeTex.getWidth() - 20, 23, backgroundLargeTex.getWidth(), backgroundLargeTex.getHeight());
        ammoLabel.setAlignment(Align.center);

        /* Tray item selector */
        selector = new HudInventoryImage(selectorTex, 100);
        selector.setPosition(stage.getWidth() - 234, 80);

        stage.addActor(inventoryHGroup);
        stage.addActor(trayHGroup);
        stage.addActor(helmet);
        stage.addActor(vest);
        stage.addActor(ammoBackground);
        stage.addActor(ammoLabel);
        stage.addActor(selector);
        stage.addActor(vestAbosrbLabel);
        stage.addActor(helmetAbsorbLabel);
    }

    @Override
    public void update() {
        resetInputProcessor();
        stage.act();
        ammoLabel.setText(this.inventory.getSelectedCellItem() != null ? inventory.getSelectedCellItem().toString() : "0 / 0");

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            Item item = inventory.getItem(i);
            invList.get(i).setDrawable(new SpriteDrawable(new Sprite(pickTexture(item))));
        }

        for (int i = 0; i < 4; i++) {
            Item item = inventory.getTray()[i];
            trayList.get(i).setDrawable(new SpriteDrawable(new Sprite(pickTexture(item))));
        }

        helmet.setDrawable(new SpriteDrawable(new Sprite(pickTexture(inventory.getHelmetArmor()))));
        vest.setDrawable(new SpriteDrawable(new Sprite(pickTexture(inventory.getVestArmor()))));

        helmetAbsorbLabel.setText(inventory.getHelmetArmor() != null ? String.valueOf(inventory.getHelmetArmor().getDamageAbsorptionLevel()) : "");
        vestAbosrbLabel.setText(inventory.getVestArmor() != null ? String.valueOf(inventory.getVestArmor().getDamageAbsorptionLevel()) : "");

        selectorPositionUpdate();
    }

    private void selectorPositionUpdate() {
        switch (inventory.getSelectedCellID()) {
            case 0:
                selector.addAction(moveTo(stage.getWidth() - 234, 80, 0.05f));
                break;
            case 1:
                selector.addAction(moveTo(stage.getWidth() - 178, 80, 0.05f));
                break;
            case 2:
                selector.addAction(moveTo(stage.getWidth() - 122, 80, 0.05f));
                break;
            case 3:
                selector.addAction(moveTo(stage.getWidth() - 66, 80, 0.05f));
                break;
        }
    }

    @Override
    public void render() {
        update();
        float progress = player.getAnimation().getReloadTime() * 50;

        stage.draw();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(stage.getViewport().getScreenX() + stage.getWidth() / 2 - progress / 2, stage.getViewport().getScreenY() + stage.getHeight() - 25, progress, 4);

        if (inventory.getHelmetArmor() != null) {
            shapeRenderer.rect(stage.getWidth() - 66, 198, 46 - (46 * inventory.getHelmetArmor().getWearLevel()), 4);
        }
        if (inventory.getVestArmor() != null) {
            shapeRenderer.rect(stage.getWidth() - 66, 142, 46 - (46 * inventory.getVestArmor().getWearLevel()), 4);
        }

        renderHP(shapeRenderer);
        shapeRenderer.end();
    }

    private void renderHP(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(new Color(0.298f, 0.686f, 0.314f, 1f));
        if (player.getHealth() <= 50) shapeRenderer.setColor(Color.ORANGE);
        if (player.getHealth() <= 25) shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(stage.getWidth() - 4 * backgroundTex.getWidth() - 50, 20, player.getHealth() * 2.14f, 6);
    }

    private Texture pickTexture(Item item) {
        return (item != null) ? item.getSquareSprite().getTexture() : backgroundTex;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(((int) (width * SCALE)), ((int) (height * SCALE)));
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTex.dispose();
        backgroundLargeTex.dispose();
        hideTex.dispose();
        showTex.dispose();
        shapeRenderer.dispose();
    }

    public void toggleInventory() {
        if (hidden) {
            toggleInventoryButton.setDrawable(new SpriteDrawable(new Sprite(hideTex)));
            inventoryHGroup.addAction(moveBy(-(INVENTORY_SIZE * 46 + INVENTORY_SIZE * 10), 0, 0.5f, Interpolation.pow2));
            hidden = false;
        } else {
            toggleInventoryButton.setDrawable(new SpriteDrawable(new Sprite(showTex)));
            inventoryHGroup.addAction(moveBy(INVENTORY_SIZE * 46 + INVENTORY_SIZE * 10, 0, 0.5f, Interpolation.pow2));
            hidden = true;
        }
    }

    @Override
    public void resetInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

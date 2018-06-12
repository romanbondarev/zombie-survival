package com.gdx.game.items;

import com.gdx.game.items.armor.Armor;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.items.weapons.ammo.Ammo;
import com.gdx.game.models.Player;
import com.gdx.game.utils.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Inventory {

    public enum Direction {
        NEXT, PREVIOUS
    }

    private Player player;
    private Armor helmetArmorCell;
    private Armor vestArmorCell;

    private Item[] inventory;
    private Item[] tray;

    private int selectedCellID = 0;

    /**
     * Creates an inventory.
     *
     * @param player whom inventory would be.
     */
    public Inventory(Player player) {
        this.player = player;
        tray = new Item[4];
        inventory = new Item[Constants.INVENTORY_SIZE];
    }

    /**
     * Executes the action with the item selected from the inventory.
     * <p>
     * Weapon: sets a weapon into selected cell (if cell contains a weapon, returns it back to the inventory)<br>
     * Ammo: loads a full ammo into the first gun found in the tray (gun and ammo must be compatible)<br>
     * Armor: sets an armor of its type into the helmet or vest armor cell.<br>
     * First aid kit: uses a kit (heals player) and removes it from the inventory
     */
    public void selectItem(Item item) {
        /* ARMOR */
        if (item instanceof Armor) {
            if (((Armor) item).getArmorType().equals(Armor.ArmorType.HEAD)) {
                removeItemFromInventory(item);
                returnItemToInventory(helmetArmorCell);
                setHelmetArmor(((Armor) item));
            }
            if (((Armor) item).getArmorType().equals(Armor.ArmorType.BODY)) {
                removeItemFromInventory(item);
                returnItemToInventory(vestArmorCell);
                setVestArmor(((Armor) item));
            }
        }

        /* WEAPON */
        if (item instanceof Weapon) {
            removeItemFromInventory(item);
            returnItemToInventory(getSelectedCellItem());
            setSelectedItem(item);
        }

        /* AMMO */
        if (item instanceof Ammo) {
            for (Item aTray : tray) {
                if (aTray instanceof Weapon) {
                    if (((Weapon) aTray).getWeaponType().equals(((Ammo) item).getWeaponType())) {
                        ((Weapon) aTray).loadAmmo(((Ammo) removeItemFromInventory(item)));
                        break;
                    }
                }
            }
        }

        /* FIRST AID KIT */
        if (item instanceof MedKit) {
            ((MedKit) item).heal(player);
            removeItemFromInventory(item);
        }
    }

    /**
     * Returns the item back to the inventory (enough space is a must)
     *
     * @param item item to return
     */
    public void returnItemToInventory(Item item) {
        if (isEmpty()) {
            // Inventory is not full
            if (item instanceof Armor) {
                if (((Armor) item).getArmorType().equals(Armor.ArmorType.HEAD)) {
                    helmetArmorCell = null;
                    addItemToInventory(item);
                }
                if (((Armor) item).getArmorType().equals(Armor.ArmorType.BODY)) {
                    vestArmorCell = null;
                    addItemToInventory(item);
                }
            }

            if (item instanceof Weapon) {
                addItemToInventory(removeItemFromTray(item));
            }
        }
    }

    /**
     * Adds the item to the inventory
     *
     * @param item item to add
     */
    public void addItemToInventory(Item item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                break;
            }
        }
    }

    /**
     * Deletes the item from the inventory
     *
     * @param item item to delete
     * @return deleted item for further actions with it, returns null if no such item is found in the inventory
     */
    public Item removeItemFromInventory(Item item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == item) {
                inventory[i] = null;
                return item;
            }
        }
        return null;
    }

    /**
     * Deletes the item from the tray.
     *
     * @param item item to delete
     * @return deleted item for further actions with it, returns null if no such item is found in the tray
     */
    public Item removeItemFromTray(Item item) {
        for (int i = 0; i < tray.length; i++) {
            if (tray[i] == item) {
                tray[i] = null;
                return item;
            }
        }
        return null;
    }

    /**
     * Sets the item to the tray cell that is selected.
     */
    public void setSelectedItem(Item item) {
        tray[selectedCellID] = item;
    }

    /**
     * Changes the selected cell id.
     *
     * @param direction NEXT - goes right, PREVIOUS - goes left
     */
    public void scrollTray(Direction direction) {
        switch (direction) {
            case NEXT:
                if (selectedCellID < 3) selectedCellID++;
                else selectedCellID = 0;
                break;
            case PREVIOUS:
                if (selectedCellID > 0) selectedCellID--;
                else selectedCellID = 3;
                break;
        }
    }

    /**
     * Gets the item from the inventory by its index.
     */
    public Item getItem(int number) {
        return number < inventory.length ? getInventory().get(number) : null;
    }

    /**
     * Gets the inventory as a list.
     */
    public List<Item> getInventory() {
        return Arrays.asList(inventory);
    }

    /**
     * Gets the tray as an array.
     */
    public Item[] getTray() {
        return tray;
    }

    /**
     * Sets currently selected cell id.
     *
     * @param selectedCellID id to be set
     */
    public void setSelectedCellID(int selectedCellID) {
        this.selectedCellID = selectedCellID;
    }

    /**
     * Gets currently selected cell id.
     */
    public int getSelectedCellID() {
        return selectedCellID;
    }

    /**
     * Gets the item that is currently selected.
     */
    public Item getSelectedCellItem() {
        return tray[getSelectedCellID()];
    }

    /**
     * Sets the helmet into the helmet armor cell.
     */
    public void setHelmetArmor(Armor helmetArmor) {
        this.helmetArmorCell = helmetArmor;
    }

    /**
     * Gets the currently worn helmet.
     */
    public Armor getHelmetArmor() {
        return helmetArmorCell;
    }

    /**
     * Sets the vest into the vest armor cell.
     */
    public void setVestArmor(Armor vestArmor) {
        this.vestArmorCell = vestArmor;
    }

    /**
     * Gets the currently worn vest.
     */
    public Armor getVestArmor() {
        return vestArmorCell;
    }

    /**
     * Checks if the inventory's empty
     */
    public boolean isEmpty() {
        return Arrays.stream(inventory).noneMatch(Objects::isNull);
    }
}

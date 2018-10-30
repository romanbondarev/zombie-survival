package com.gdx.game.items;

import com.gdx.game.items.armor.Armor;
import com.gdx.game.items.weapons.Weapon;
import com.gdx.game.items.weapons.ammo.Ammo;
import com.gdx.game.models.Player;
import com.gdx.game.utils.Constants;

import java.util.Arrays;
import java.util.List;

public class Inventory {

    public enum Direction {
        NEXT, PREVIOUS
    }

    private Player player;
    private Armor helmetArmor;
    private Armor vestArmor;

    private Item[] inventory;
    private Item[] tray;

    private int selectedCellInt = 0;

    public Inventory(Player player) {
        this.player = player;
        tray = new Item[4];
        inventory = new Item[Constants.INVENTORY_SIZE];
    }

    public void selectItem(Item item) {
        /* ARMOR */
        if (item instanceof Armor) {
            if (((Armor) item).getArmorType().equals(Armor.ArmorType.HEAD)) {
                removeItemFromInventory(item);
                returnItemToInventory(helmetArmor);
                setHelmetArmor(((Armor) item));
            }
            if (((Armor) item).getArmorType().equals(Armor.ArmorType.BODY)) {
                removeItemFromInventory(item);
                returnItemToInventory(vestArmor);
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

        /* MEDKIT */
        if (item instanceof MedKit) {
            ((MedKit) item).heal(player);
            removeItemFromInventory(item);
        }
    }

    public void returnItemToInventory(Item item) {
        boolean canReturn = false;
        for (Item invItem : inventory) {
            if (invItem == null) canReturn = true;
        }

        if (canReturn) {
            if (item instanceof Armor) {
                if (((Armor) item).getArmorType().equals(Armor.ArmorType.HEAD)) {
                    helmetArmor = null;
                    addItemToInventory(item);
                }
                if (((Armor) item).getArmorType().equals(Armor.ArmorType.BODY)) {
                    vestArmor = null;
                    addItemToInventory(item);
                }
            }

            if (item instanceof Weapon) {
                addItemToInventory(removeItemFromTray(item));
            }
        }
    }

    public void addItemToInventory(Item item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                break;
            }
        }
    }

    public Item removeItemFromInventory(Item item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == item) {
                inventory[i] = null;
                return item;
            }
        }
        return null;
    }

    public Item removeItemFromTray(Item item) {
        for (int i = 0; i < tray.length; i++) {
            if (tray[i] == item) {
                tray[i] = null;
                return item;
            }
        }
        return null;
    }

    public void setSelectedItem(Item item) {
        tray[selectedCellInt] = item;
    }

    public void scrollTray(Direction direction) {
        switch (direction) {
            case NEXT:
                if (selectedCellInt < 3) selectedCellInt++;
                else selectedCellInt = 0;
                break;
            case PREVIOUS:
                if (selectedCellInt > 0) selectedCellInt--;
                else selectedCellInt = 3;
                break;
        }
    }

    public Item getItem(int number) {
        return number < inventory.length ? getInventory().get(number) : null;
    }

    public Armor getVestArmor() {
        return vestArmor;
    }

    public Armor getHelmetArmor() {
        return helmetArmor;
    }

    public List<Item> getInventory() {
        return Arrays.asList(inventory);
    }

    public Item[] getTray() {
        return tray;
    }

    public Item getSelectedCellItem() {
        return tray[getSelectedCellInt()];
    }

    public int getSelectedCellInt() {
        return selectedCellInt;
    }

    public void setSelectedCellInt(int selectedCellInt) {
        this.selectedCellInt = selectedCellInt;
    }

    public void setHelmetArmor(Armor helmetArmor) {
        this.helmetArmor = helmetArmor;
    }

    public void setVestArmor(Armor vestArmor) {
        this.vestArmor = vestArmor;
    }

    public boolean isEmpty() {
        for (Item item : inventory) {
            if (item == null) return false;
        }
        return true;
    }
}

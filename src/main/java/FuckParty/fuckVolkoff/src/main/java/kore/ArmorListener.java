package FuckParty.fuckVolkoff.src.main.java.kore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmorListener implements Listener {

    private final List<String> blockedMaterials;

    public ArmorListener(List<String> blockedMaterials){
        this.blockedMaterials = blockedMaterials;
    }
    //Event Priority is highest because other plugins might cancel the events before we check.

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent e){
        boolean shift = false, numberkey = false;
        if(e.isCancelled()) return;
        if(e.getAction() == InventoryAction.NOTHING) return;// Why does this get called if nothing happens??
        if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
            shift = true;
        }
        if(e.getClick().equals(ClickType.NUMBER_KEY)){
            numberkey = true;
        }
        if(e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!(e.getWhoClicked() instanceof Player)) return;
        ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
        if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()){
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
            return;
        }
        if(shift){
            newArmorType = ArmorType.matchType(e.getCurrentItem());
            if(newArmorType != null){
                boolean equipping = true;
                if(e.getRawSlot() == newArmorType.getSlot()){
                    equipping = false;
                }
                if(newArmorType.equals(ArmorType.HELMET) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet()) : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate()) : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings()) : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())) || newArmorType.equals(ArmorType.BOOTS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getBoots()) : !isAirOrNull(e.getWhoClicked().getInventory().getBoots()))){
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        e.setCancelled(true);
                    }
                }
            }
        }else{
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if(numberkey){
                if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if(!isAirOrNull(hotbarItem)){// Equipping
                        newArmorType = ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }else{// Unequipping
                        newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }else{
                if(isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())){// unequip with no new item going into the slot.
                    newArmorType = ArmorType.matchType(e.getCurrentItem());
                }
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                // newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
            }
            if(numberkey && newArmorType == null && oldArmorPiece != null){
                if(e.getSlotType() != InventoryType.SlotType.ARMOR){
                    return;
                }
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, null, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()){
                    e.setCancelled(true);
                }
                return;
            }else if(numberkey && newArmorType != null && oldArmorPiece != null && newArmorType.getSlot() != e.getRawSlot()){
                if(e.getSlotType() != InventoryType.SlotType.ARMOR){
                    return;
                }
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, null, oldArmorPiece, null);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()){
                    e.setCancelled(true);
                }
                return;
            }

            if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()){
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
                if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        if (e.isCancelled()) return;
        if(e.getAction() == Action.PHYSICAL) return;
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){// Having both of these checks is useless, might as well do it though.
                // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                Material mat = e.getClickedBlock().getType();

                switch(mat){
                    case STONE_BUTTON:
                    case WOOD_BUTTON:
                    case LEVER:
                    case BEACON:
                    case SIGN:
                    case WALL_SIGN:
                    case SIGN_POST:
                    case DAYLIGHT_DETECTOR_INVERTED:
                    case DAYLIGHT_DETECTOR:
                    case JUKEBOX:
                    case ANVIL:
                    case HOPPER:
                    case CAULDRON:
                    case BED:
                    case BED_BLOCK:
                    case NOTE_BLOCK:
                    case ENCHANTMENT_TABLE:
                        return;
                }

                if(mat.toString().contains("DOOR"))return;

                if(mat.toString().contains("FENCE"))return;

                if(mat.toString().contains("BREWING"))return;

                if(mat.toString().contains("WORKBENCH"))return;

                if(mat.toString().contains("FURNACE"))return;

                if(mat.toString().contains("DIODE"))return;

                if(mat.toString().contains("COMPARATOR"))return;

                if(mat.toString().contains("CHEST"))return;
            }
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if(newArmorType != null){
                if(newArmorType.equals(ArmorType.HELMET) && isAirOrNull(e.getPlayer().getInventory().getHelmet()) || newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(e.getPlayer().getInventory().getChestplate()) || newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(e.getPlayer().getInventory().getLeggings()) || newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(e.getPlayer().getInventory().getBoots())){
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event){
        // getType() seems to always be even.
        // Old Cursor gives the item you are equipping
        // Raw slot is the ArmorType slot
        // Can't replace armor using this method making getCursor() useless.
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if(event.getRawSlots().isEmpty()) return;// Idk if this will ever happen
        if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
        // Debug shit
		/*Sy("Slots: " + event.getInventorySlots().toString());
		Sy("Raw Slots: " + event.getRawSlots().toString());
		if(event.getCursor() != null){
			Sy("Cursor: " + event.getCursor().getType().name());
		}
		if(event.getOldCursor() != null){
			Sy("OldCursor: " + event.getOldCursor().getType().name());
		}
		Sy("Type: " + event.getType().name());*/
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e){
        ArmorType type = ArmorType.matchType(e.getBrokenItem());
        if(type != null){
            Player p = e.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if(type.equals(ArmorType.HELMET)){
                    p.getInventory().setHelmet(i);
                }else if(type.equals(ArmorType.CHESTPLATE)){
                    p.getInventory().setChestplate(i);
                }else if(type.equals(ArmorType.LEGGINGS)){
                    p.getInventory().setLeggings(i);
                }else if(type.equals(ArmorType.BOOTS)){
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();
        for(ItemStack i : p.getInventory().getArmorContents()){
            if(!isAirOrNull(i)){
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
                // No way to cancel a death event.
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    private boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().equals(Material.AIR);
    }
}

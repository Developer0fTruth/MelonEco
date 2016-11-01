package com.turqmelon.MelonEco.listeners;

import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.CurrencyTicket;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Turqmelon on 11/1/16.
 */
public class InteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Account account = AccountManager.getAccount(player);
        if (account == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack hand = player.getItemInHand();
            if (hand != null && hand.getType() == Material.PAPER) {
                try {
                    CurrencyTicket ticket = new CurrencyTicket(hand);
                    event.setCancelled(true);
                    int inHand = hand.getAmount();

                    inHand--;
                    if (inHand > 0) {
                        hand = hand.clone();
                        hand.setAmount(inHand);
                        player.setItemInHand(hand);
                    } else {
                        player.setItemInHand(null);
                    }
                    player.updateInventory();

                    ticket.redeem(account);

                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    player.sendMessage("§a§l[Eco] §aYou redeemed a money ticket!");

                } catch (Exception ignored) {
                }
            }
        }
    }

}

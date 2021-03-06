package rina.turok.bope.bopemod.hacks.combat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import rina.turok.bope.Bope;
import rina.turok.bope.bopemod.BopeMessage;
import rina.turok.bope.bopemod.BopeModule;
import rina.turok.bope.bopemod.guiscreen.settings.BopeSetting;
import rina.turok.bope.bopemod.hacks.BopeCategory;
import rina.turok.bope.bopemod.util.BopeUtilItem;

public class BopeAutoOffHandCrystal extends BopeModule {
   BopeSetting absolute = this.create("Absolute", "AutoOffhandCrystalAbsolute", true);
   BopeSetting enable_totem = this.create("Auto Enable Totem", "AutoOffhandCrystalEnableAutoTotem", true);
   BopeSetting slider_smart = this.create("Smart", "AutoOffhandCrystalSmart", 1, 0, 10);
   boolean is_smart_ev = false;
   String last = "default";
   int crystal_stack;
   int last_slot;

   public BopeAutoOffHandCrystal() {
      super(BopeCategory.BOPE_COMBAT, true);
      this.name = "Auto Off Hand Crystal";
      this.tag = "AutoOffHandCrystal";
      this.description = "Automatically replace in offhand crystal.";
      this.release("B.O.P.E - Module - B.O.P.E");
   }

   public void enable() {
      this.last_slot = -1;
      if (this.absolute.get_value(true)) {
         boolean totem = false;
         boolean gapple = false;
         if (Bope.module_is_active("AutoTotem")) {
            totem = true;
            Bope.get_module("AutoTotem").set_disable();
         }

         if (Bope.module_is_active("AutoGapple")) {
            boolean cancel = false;
            gapple = true;
            if (Bope.get_setting("AutoGapple", "AutoGappleEnableAutoTotem").get_value(true)) {
               Bope.get_setting("AutoGapple", "AutoGappleEnableAutoTotem").set_value(false);
               cancel = true;
            }

            Bope.get_module("AutoGapple").set_disable();
            if (cancel) {
               Bope.get_setting("AutoGapple", "AutoGappleEnableAutoTotem").set_value(true);
            }
         }

         if (totem || gapple) {
            StringBuilder message = new StringBuilder();
            if (totem && gapple) {
               message.append(Bope.dg + "AutoTotem & AutoGapple" + Bope.r + " has been " + Bope.re + "disabled");
            } else if (totem) {
               message.append(Bope.dg + "AutoTotem" + Bope.r + " has been " + Bope.re + "disabled");
            } else if (gapple) {
               message.append(Bope.dg + "AutoGapple" + Bope.r + " has been " + Bope.re + "disabled");
            }

            BopeMessage.send_client_message(message.toString());
         }
      }

   }

   public void disable() {
      if (this.enable_totem.get_value(true) && !Bope.module_is_active("AutoTotem")) {
         Bope.get_module("AutoTotem").set_active(true);
      }

   }

   public void update() {
      if (this.mc.player != null && this.mc.world != null) {
         this.crystal_stack = this.mc.player.inventory.mainInventory.stream().filter((item) -> {
            return item.getItem() == Items.END_CRYSTAL;
         }).mapToInt(ItemStack::getCount).sum();
         if (this.mc.currentScreen instanceof GuiContainer) {
            return;
         }

         if (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL && this.mc.player.getHealth() <= (float)this.slider_smart.get_value(1.0D)) {
            this.is_smart_ev = true;
            this.set_active(false);
         } else if (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return;
         }

         int crystal_slot = BopeUtilItem.get_item_slot(Items.END_CRYSTAL);
         if (crystal_slot == -1) {
            return;
         }

         BopeUtilItem.set_offhand_item(crystal_slot);
      }

   }
}

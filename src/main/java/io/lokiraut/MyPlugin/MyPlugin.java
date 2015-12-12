package io.lokiraut.MyPlugin;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.EnumTraits;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisplayNameData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAchievementData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;

import java.util.List;
import java.util.Map;

/**
 * Created by Lokio on 12/3/2015.
 */



@Plugin(id="MyPlugin", name="MyPlugin", version = "1.0")
public class MyPlugin {
    private Logger logger;
    private Game game;
    @Inject
    private void setLogger(Logger logger){
        this.logger = logger;
    }
    @Inject
    private void setGame(Game game){
        this.game = game;
    }
    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("test");
    }
    @Listener
    public void onClientConnectionEvent(ClientConnectionEvent.Join event) {
        logger.info("Player Jon!");
        Player joined = event.getTargetEntity();
        Text msg = Texts.of("Hi " + joined.getName() + "!");
        event.setMessage(msg);
    }
    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event){
        /*Map<String,Object> test = event.getCause().getNamedCauses();


        for (Map.Entry<String, Object> entry : test.entrySet())
        {
            String playerName =  ((Player) entry.getValue()).getName();
            logger.info("Player \"" + playerName + "\" interacted with block " + event.getTargetBlock().getState().getType().getName());

        }*/
        if(event.getCause().root().isPresent()){
            if(event.getCause().root().get() instanceof Player){
                Player player = (Player) event.getCause().root().get();
                if(player.getItemInHand().isPresent()){
                    if(player.getItemInHand().get().getItem() == ItemTypes.BLAZE_ROD){
                        /*Location blockLoco = event.getTargetBlock().getLocation().get();
                        blockLoco = blockLoco.setPosition(blockLoco.getPosition().add(0,1,0));
                        blockLoco.setBlock(BlockTypes.FIRE.getDefaultState());*/
                        Object[] test = player.getItemInHand().get().createSnapshot().getManipulators().toArray();
                        for(int i = 0; i < test.length; i++){
                            if(test[i] instanceof ImmutableDisplayNameData){
                                ImmutableDisplayNameData tet = (ImmutableDisplayNameData) test[i];
                                if(Texts.toPlain(tet.displayName().get()).equalsIgnoreCase("noise maker")){
                                    player.getWorld().playSound(SoundTypes.FIREWORK_LAUNCH,player.getLocation().getPosition(), 1.0, 1.0);
                                }
                            }

                        }
                        //player.getWorld().playSound(SoundTypes.ORB_PICKUP,player.getLocation().getPosition(), 1.0, 1.0);
                        event.setCancelled(true);
                    }
                }
            }
        }



    }

    @Listener
    public void onPrimaryInteract(InteractBlockEvent.Primary event){

    }
    @Listener
    public void onBreakAttempt(ChangeBlockEvent.Break event){
        //event.setCancelled(true);
    }
}

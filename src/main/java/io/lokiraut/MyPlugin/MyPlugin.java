package io.lokiraut.MyPlugin;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.EnumTraits;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisplayNameData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDyeableData;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAchievementData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.Ageable;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.animal.Sheep;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.weather.Weathers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Lokio on 12/3/2015.
 */



@Plugin(id="MyPlugin", name="MyPlugin", version = "1.0")
public class MyPlugin {
    @Inject
    private Logger logger;
    @Inject
    private Game game;

    //public SqlService sqlService = game.getServiceManager().provide(SqlService.class).get();

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("Plugin is running, rejoice!");
        //try {
            //Connection test = sqlService.getDataSource("jdbc:sqlite:config/MyPlugin/testdb.db").getConnection();
            //test.prepareStatement("CREATE TABLE testing123 (id INTEGER PRIMARY KEY, test TEXT)");
            //test.prepareStatement("INSERT INTO testing123 VALUES(1, \"Test Sause\")");
           // test.close();
        //}catch(SQLException e){

        //}
    }
    @Listener
    public void onClientConnectionEvent(ClientConnectionEvent.Join event) {
        logger.info("Player Jon!");
        Player joined = event.getTargetEntity();
        Text msg = Texts.of("Hi " + joined.getName() + "!");
        event.setMessage(msg);
        Team myTeam = Team.builder().name("test").displayName(Texts.of("Meep")).build();
        List<Team> teams  = new ArrayList<Team>(){};
        teams.add(myTeam);
        List<Objective> objectives = new ArrayList<Objective>();
        objectives.add(Objective.builder().displayName(Texts.of("Teet")).name("Teet").criterion(Criteria.DEATHS).build());

        Scoreboard myScoreboard = Scoreboard.builder().teams(teams).objectives(objectives).build();
        joined.setScoreboard(myScoreboard);

        //joined.
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Secondary event){
        if(event.getCause().root() instanceof Player){
            Player player = (Player) event.getCause().root();
            if(player.getItemInHand().isPresent()){
                if(player.getItemInHand().get().getItem() == ItemTypes.BLAZE_ROD){
                        /*Location blockLoco = event.getTargetBlock().getLocation().get();
                        blockLoco = blockLoco.setPosition(blockLoco.getPosition().add(0,1,0));
                        blockLoco.setBlock(BlockTypes.FIRE.getDefaultState());*/
                    Object[] test = player.getItemInHand().get().createSnapshot().getManipulators().toArray();
                    for(int i = 0; i < test.length; i++){
                        if(test[i] instanceof ImmutableDisplayNameData){
                            ImmutableDisplayNameData tet = (ImmutableDisplayNameData) test[i];
                            if(Texts.toPlain(tet.displayName().get()).equalsIgnoreCase("entity deleter")){
                                if(event.getTargetEntity() instanceof Player){
                                    logger.warn("tried eating a player");
                                    return;
                                }
                                event.getTargetEntity().remove();
                                event.setCancelled(true);
                            }else if(Texts.toPlain(tet.displayName().get()).equalsIgnoreCase("lokio tool")){
                                if(event.getTargetEntity() instanceof Player){
                                    Player plr = (Player) event.getTargetEntity();
                                    if(plr.getName().equalsIgnoreCase("Lokio27"))
                                        return;
                                    GameModeData gmd = plr.getGameModeData();
                                    if(gmd.get(Keys.GAME_MODE).get().equals(GameModes.CREATIVE)){
                                        plr.offer(Keys.GAME_MODE,GameModes.SURVIVAL);
                                        logger.info("Set " + plr.getName() + " to survial");
                                    }else if(gmd.get(Keys.GAME_MODE).get().equals(GameModes.SURVIVAL)){
                                        plr.offer(Keys.GAME_MODE,GameModes.CREATIVE);
                                        logger.info("set " + plr.getName() + "  to creat");
                                    }
                                    //plr
                                    event.setCancelled(true);
                                }
                            }
                        }

                    }
                    //player.getWorld().playSound(SoundTypes.ORB_PICKUP,player.getLocation().getPosition(), 1.0, 1.0);
                }
            }
        }
    }

    @Listener
    public void onWeather(ChangeWorldWeatherEvent event){
        if(event.getWeather() == Weathers.THUNDER_STORM)
            return;

        //event.setWeather(Weathers.THUNDER_STORM);
        logger.info("boom");
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event){
        /*Map<String,Object> test = event.getCause().getNamedCauses();


        for (Map.Entry<String, Object> entry : test.entrySet())
        {
            String playerName =  ((Player) entry.getValue()).getName();
            logger.info("Player \"" + playerName + "\" interacted with block " + event.getTargetBlock().getState().getType().getName());

        }*/
        //if(event.getCause().root()){
            if(event.getCause().root() instanceof Player){
                Player player = (Player) event.getCause().root();
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
                                    SoundType[] sounds = {SoundTypes.FIREWORK_LAUNCH,SoundTypes.FIREWORK_BLAST,SoundTypes.GLASS,SoundTypes.EXPLODE,SoundTypes.COW_IDLE,SoundTypes.SHEEP_IDLE,SoundTypes.PIG_IDLE};
                                    player.getWorld().playSound(sounds[(int) Math.round(Math.random()*(sounds.length-1))],player.getLocation().getPosition(), 1.0, 1.0);
                                    event.setCancelled(true);
                                }
                            }

                        }
                        //player.getWorld().playSound(SoundTypes.ORB_PICKUP,player.getLocation().getPosition(), 1.0, 1.0);
                    }
                }
            }
        //}



    }

    @Listener
    public void onPrimaryInteract(InteractBlockEvent.Primary event){

    }
    @Listener
    public void onBreakAttempt(ChangeBlockEvent.Break event){
        //if(event.getCause().root().isPresent()) {
            if(event.getCause().root() instanceof PrimedTNT||event.getCause().root() instanceof Explosion) {
                logger.info("boom");
                event.setCancelled(true);
            }
        //}
    }
}

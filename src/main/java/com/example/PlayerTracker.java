package com.example;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.*;

public class PlayerTracker {
    private static List<PlayerData> currentList = new ArrayList<>();
    private static Map<String, PendingRtp> pendingRtp = new LinkedHashMap<>();
    private static Set<String> confirmedButtonClick = new HashSet<>();
    private static Map<BlockPos, ButtonPressEvent> buttonPressEvents = new HashMap<>();
    private static long lastTickTime = 0L;
    private static final long TICK_INTERVAL_MS = 100L;

    public static class PendingRtp {
        public PlayerData data;
        public long clickedAt;
        public boolean confirmedClick;
        
        public PendingRtp(PlayerData data, long clickedAt) {
            this.data = data;
            this.clickedAt = clickedAt;
            this.confirmedClick = true;
        }
    }

    public static class ButtonPressEvent {
        public BlockPos pos;
        public long pressedAt;
        public boolean wasUnpowered;
        
        public ButtonPressEvent(BlockPos pos, long time) {
            this.pos = pos;
            this.pressedAt = time;
            this.wasUnpowered = true;
        }
    }

    // Śledź poprzedni stan buttonów
    private static Map<BlockPos, Boolean> previousButtonStates = new HashMap<>();

    public static void tick() {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_INTERVAL_MS) {
            lastTickTime = now;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null) {
                List<PlayerData> newList = new ArrayList<>();
                Set<String> currentNames = new HashSet<>();

                // Wykryj nowo wciśnięte buttony
                detectNewButtonPresses(client, now);

                // Zbierz wszystkich graczy z wymaganymi itemami
                Map<String, PlayerEntity> playersWithItems = new HashMap<>();
                for (PlayerEntity player : client.world.getPlayers()) {
                    if (player != client.player && !BlacklistTracker.isBlacklisted(player.getName().getString())) {
                        String name = player.getName().getString();
                        PlayerData pd = new PlayerData(player);
                        
                        if (!ItemChecker.hasRequiredItems(pd)) {
                            continue;
                        }
                        
                        currentNames.add(name);
                        newList.add(0, pd);
                        playersWithItems.put(name, player);
                    }
                }

                // Dla każdego nowo wciśniętego buttona, znajdź kto go kliknął
                Iterator<Map.Entry<BlockPos, ButtonPressEvent>> buttonIt = buttonPressEvents.entrySet().iterator();
                while (buttonIt.hasNext()) {
                    Map.Entry<BlockPos, ButtonPressEvent> entry = buttonIt.next();
                    ButtonPressEvent event = entry.getValue();
                    
                    // Event jest nowy (w ostatnich 500ms)
                    if (now - event.pressedAt < 500 && event.wasUnpowered) {
                        // Znajdź gracza który kliknął ten button
                        PlayerEntity clicker = findButtonClicker(event.pos, playersWithItems.values(), client);
                        
                        if (clicker != null) {
                            String name = clicker.getName().getString();
                            PlayerData pd = new PlayerData(clicker);
                            pd.nearButton = true;
                            pd.lastSeenNearButton = now;
                            
                            confirmedButtonClick.add(name);
                            pendingRtp.put(name, new PendingRtp(pd, now));
                            
                            // Oznacz event jako przetworzony
                            event.wasUnpowered = false;
                        }
                    }
                    
                    // Usuń stare eventy
                    if (now - event.pressedAt > 2000) {
                        buttonIt.remove();
                    }
                }

                // Sprawdź czy gracze którzy KLIKNĘLI button zniknęli
                Iterator<Map.Entry<String, PendingRtp>> it = pendingRtp.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, PendingRtp> entry = it.next();
                    String name = entry.getKey();
                    PendingRtp pending = entry.getValue();
                    long elapsed = now - pending.clickedAt;

                    if (!currentNames.contains(name)) {
                        // Gracz zniknął po kliknięciu buttona!
                        if (pending.confirmedClick && elapsed <= 15000) {
                            RtpTracker.markRtp(pending.data);
                        }
                        it.remove();
                        confirmedButtonClick.remove(name);
                    } else if (elapsed > 20000) {
                        // Za długo - nie zniknął
                        it.remove();
                        confirmedButtonClick.remove(name);
                    } else {
                        // Aktualizuj dane gracza
                        for (PlayerData pd : newList) {
                            if (pd.name.equals(name)) {
                                pending.data = pd;
                                break;
                            }
                        }
                    }
                }

                currentList = newList;
            }
        }
    }

    private static void detectNewButtonPresses(MinecraftClient client, long now) {
        if (client.player == null || client.world == null) return;
        
        BlockPos playerPos = client.player.getBlockPos();
        int radius = 30;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    try {
                        BlockState state = client.world.getBlockState(checkPos);
                        if (state.getBlock() == Blocks.STONE_BUTTON) {
                            boolean isPowered = state.get(ButtonBlock.POWERED);
                            Boolean wasPowered = previousButtonStates.get(checkPos);
                            
                            // Wykryj NOWE wciśnięcie (było OFF, teraz ON)
                            if (isPowered && (wasPowered == null || !wasPowered)) {
                                buttonPressEvents.put(checkPos, new ButtonPressEvent(checkPos, now));
                            }
                            
                            previousButtonStates.put(checkPos, isPowered);
                        }
                    } catch (Exception e) {
                        // Chunk not loaded
                    }
                }
            }
        }
        
        // Wyczyść stare stany buttonów
        if (previousButtonStates.size() > 1000) {
            previousButtonStates.clear();
        }
    }

    private static PlayerEntity findButtonClicker(BlockPos buttonPos, Collection<PlayerEntity> players, MinecraftClient client) {
        PlayerEntity closest = null;
        double closestScore = Double.MAX_VALUE;
        
        Vec3d buttonCenter = Vec3d.ofCenter(buttonPos);
        
        for (PlayerEntity player : players) {
            Vec3d playerPos = player.getEyePos();
            double distance = playerPos.distanceTo(buttonCenter);
            
            // Musi być w zasięgu interakcji (max 4.5 bloki)
            if (distance > 4.5) continue;
            
            // Sprawdź czy gracz patrzy na button (raycast)
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d endPos = playerPos.add(lookVec.multiply(5.0));
            
            BlockHitResult hit = client.world.raycast(new RaycastContext(
                playerPos, endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
            ));
            
            boolean lookingAtButton = hit.getType() == HitResult.Type.BLOCK && 
                                      hit.getBlockPos().equals(buttonPos);
            
            // Sprawdź czy gracz wykonuje animację ręki (swing)
            boolean isSwinging = player.handSwinging;
            
            // Oblicz wynik - im bliżej i lepiej patrzy, tym lepiej
            double score = distance;
            if (lookingAtButton) score -= 2.0; // Bonus za patrzenie na button
            if (isSwinging) score -= 1.0; // Bonus za animację ręki
            
            // Najlepszy kandydat: blisko + patrzy na button + swing
            if (lookingAtButton && distance < 4.0) {
                if (score < closestScore) {
                    closestScore = score;
                    closest = player;
                }
            }
        }
        
        return closest;
    }

    public static List<PlayerData> getCurrentList() {
        return currentList;
    }

    public static int getPendingCount() {
        return pendingRtp.size();
    }
    
    public static List<PlayerData> getPendingPlayers() {
        List<PlayerData> result = new ArrayList<>();
        for (PendingRtp pending : pendingRtp.values()) {
            result.add(pending.data);
        }
        return result;
    }
    
    public static void reset() {
        pendingRtp.clear();
        confirmedButtonClick.clear();
        buttonPressEvents.clear();
        previousButtonStates.clear();
        currentList.clear();
    }
}

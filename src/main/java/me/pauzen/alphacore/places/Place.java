/*
 *  Created by Filip P. on 2/10/15 6:57 PM.
 */

package me.pauzen.alphacore.places;

import me.pauzen.alphacore.abilities.Ability;
import me.pauzen.alphacore.commands.Command;
import me.pauzen.alphacore.effects.Effect;
import me.pauzen.alphacore.core.modules.ManagerModule;
import me.pauzen.alphacore.places.events.PlaceJoinEvent;
import me.pauzen.alphacore.places.events.PlaceLeaveEvent;
import me.pauzen.alphacore.players.CorePlayer;
import me.pauzen.alphacore.utils.AllowanceChecker;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Place implements ManagerModule {

    private Place superPlace;

    private Set<CorePlayer> players = new HashSet<>();

    private String name;

    private AllowanceChecker<String>      commandChecker     = new AllowanceChecker<>();
    private AllowanceChecker<PlaceAction> placeActionChecker = new AllowanceChecker<>();

    private Set<Ability> placeAbilities = new HashSet<>();
    private Set<Effect>  placeEffects   = new HashSet<>();

    public Place(String name, Place superPlace) {
        this.name = name;
        this.superPlace = superPlace;
    }

    public void activateAbility(Ability ability) {
        placeAbilities.add(ability);
    }

    public void deactivateAbility(Ability ability) {
        placeAbilities.remove(ability);
    }

    public void activateEffect(Effect effect) {
        placeEffects.add(effect);
    }

    public void deactivateEffect(Effect effect) {
        placeEffects.remove(effect);
    }

    public boolean hasActivated(Ability ability) {
        return placeAbilities.contains(ability);
    }

    public boolean hasActivated(Effect effect) {
        return placeEffects.contains(effect);
    }

    public void applyEffect(Effect effect, int length) {
        for (CorePlayer player : this.players) {
            effect.apply(player, length);
        }
    }

    public void removeEffect(Effect effect) {
        for (CorePlayer player : this.players) {
            effect.remove(player);
        }
    }

    public Set<Ability> getActiveAbilities() {
        return placeAbilities;
    }

    public Set<Effect> getActiveEffects() {
        return placeEffects;
    }

    public AllowanceChecker<String> getCommandChecker() {
        return commandChecker;
    }

    public boolean shouldRun(Command command) {
        for (String name : command.getNames()) {
            if (!commandChecker.isAllowed(name)) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public Set<CorePlayer> getPlayers() {
        return players;
    }

    public void addPlayer(CorePlayer corePlayer) {
        new PlaceJoinEvent(corePlayer, this).call();
        getPlayers().add(corePlayer);
    }

    public void removePlayer(CorePlayer corePlayer) {
        new PlaceLeaveEvent(corePlayer, this).call();
        getPlayers().remove(corePlayer);
    }

    public boolean isAllowed(PlaceAction placeAction) {
        Stack<Place> placeStack = new Stack<>();
        Place currentPlace = this;
        while (currentPlace != null) {
            placeStack.add(currentPlace);
            currentPlace = currentPlace.getSuperPlace();
        }

        boolean allowed = true;
        for (Place place : placeStack) {
            if (place.getPlaceActionChecker().allowed(placeAction)) {
                allowed = false;
            }
            if (place.getPlaceActionChecker().disallowed(placeAction)) {
                allowed = false;
            }
        }
        return allowed;
    }

    public AllowanceChecker<PlaceAction> getPlaceActionChecker() {
        return placeActionChecker;
    }

    @Override
    public void unload() {
        PlaceManager.getManager().unregisterModule(this);
    }

    public Place getSuperPlace() {
        return superPlace;
    }
}
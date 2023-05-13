package edu.ktu.pettrackerclient.events;

import java.util.List;

import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.zones.Zone;

public interface EventFiltersDelegation {
    public void myMethod(List<Pet> pets, List<PetGroup> groups, List<Zone> zones, Long from_date, Long to_date);

}

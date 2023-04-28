package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.Zone;

public interface EventFiltersDelegation {
    public void myMethod(List<Pet> pets, List<PetGroup> groups, List<Zone> zones, Long from_date, Long to_date);

}

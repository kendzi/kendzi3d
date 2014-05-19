package kendzi.kendzi3d.world;

/**
 * Allows postpone build of world object until it is required by rendering or
 * exporting.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface BuildableWorldObject extends WorldObject {

    /**
     * Creates world object from initial data.
     */
    void buildWorldObject();

    /**
     * Checks if world object was created.
     * 
     * @return is world object created
     */
    boolean isWorldObjectBuild();
}

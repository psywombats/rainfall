/**
 *  EventMDO.java
 *  Created on Nov 12, 2012 4:45:16 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogueschema.characters;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mrogueschema.characters.data.StatsMDO;
import net.wombatrpgs.mrogueschema.graphics.DirMDO;

/**
 * An interactive component on the map is called an "Event." (it's an entity,
 * but let's pretend, okay?)
 */
@Path("characters/")
public class CharacterEventMDO extends MainSchema {
	
	@Desc("Animation - what this event looks like")
	@SchemaLink(DirMDO.class)
	@Nullable
	public String appearance;
	
//	@Desc("Hurt sound - plays when this character is damaged")
//	@SchemaLink(SoundMDO.class)
//	@Nullable
//	public String soundHurt;
	
	@Desc("Stats - RPG-like character base stats")
	@InlineSchema(StatsMDO.class)
	public StatsMDO stats;

}

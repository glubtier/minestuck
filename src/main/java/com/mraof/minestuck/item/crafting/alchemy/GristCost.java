package com.mraof.minestuck.item.crafting.alchemy;

import com.google.gson.JsonObject;
import com.mraof.minestuck.item.crafting.MSRecipeTypes;
import com.mraof.minestuck.jei.JeiGristCost;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GristCost extends GristCostRecipe
{
	public final ImmutableGristSet cost;
	private JeiGristCost jeiCost;
	
	public GristCost(ResourceLocation id, Ingredient ingredient, GristSet cost, Integer priority)
	{
		super(id, ingredient, priority);
		this.cost = cost.asImmutable();
	}
	
	@Override
	public GristSet getGristCost(ItemStack input, GristType wildcardType, boolean shouldRoundDown, World world)
	{
		return scaleToCountAndDurability(cost.copy(), input, shouldRoundDown).asImmutable();
	}
	
	@Override
	public JeiGristCost getJeiCost()
	{
		if(jeiCost == null)
			jeiCost = new JeiGristCost.Set(cost);
		return jeiCost;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return MSRecipeTypes.GRIST_COST;
	}
	
	public static class Serializer extends AbstractSerializer<GristCost>
	{
		@Override
		protected GristCost read(ResourceLocation recipeId, JsonObject json, Ingredient ingredient, Integer priority)
		{
			GristSet cost = GristSet.deserialize(json.getAsJsonObject("grist_cost"));
			return new GristCost(recipeId, ingredient, cost, priority);
		}
		
		@Override
		protected GristCost read(ResourceLocation recipeId, PacketBuffer buffer, Ingredient ingredient, int priority)
		{
			GristSet cost = GristSet.read(buffer);
			return new GristCost(recipeId, ingredient, cost, priority);
		}
		
		@Override
		public void write(PacketBuffer buffer, GristCost recipe)
		{
			super.write(buffer, recipe);
			recipe.cost.write(buffer);
		}
	}
}
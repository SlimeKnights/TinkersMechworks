package tmechworks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class DynamoModel extends ModelBase
{
    //fields
    ModelRenderer Dynamo;
    ModelRenderer CaseTop;
    ModelRenderer CaseButtonTop;
    ModelRenderer CaseSide1;
    ModelRenderer CaseSide2;
    ModelRenderer CaseBottom;
    ModelRenderer CaseButtonBottom;

    public DynamoModel()
    {
        Dynamo = new ModelRenderer(this, 0, 0);
        Dynamo.addBox(-2F, 0F, -2F, 4, 8, 4);
        Dynamo.setRotationPoint(0F, 4F, 0F);
        Dynamo.mirror = true;
        setRotation(Dynamo, 0F, 0F, 0F);
        CaseTop = new ModelRenderer(this, 16, 3);
        CaseTop.addBox(-2F, 0F, -2F, 4, 1, 4);
        CaseTop.setRotationPoint(0F, 1F, 0F);
        CaseTop.mirror = true;
        setRotation(CaseTop, 0F, 0F, 0F);
        CaseButtonTop = new ModelRenderer(this, 20, 0);
        CaseButtonTop.addBox(-1F, 0F, -1F, 2, 1, 2);
        CaseButtonTop.setRotationPoint(0F, 0F, 0F);
        CaseButtonTop.mirror = true;
        setRotation(CaseButtonTop, 0F, 0F, 0F);
        CaseSide1 = new ModelRenderer(this, 0, 12);
        CaseSide1.addBox(5F, 0F, -3F, 1, 3, 6);
        CaseSide1.setRotationPoint(0F, 6F, 0F);
        CaseSide1.mirror = true;
        setRotation(CaseSide1, 0F, 0F, 0F);
        CaseSide2 = new ModelRenderer(this, 14, 12);
        CaseSide2.addBox(5F, 0F, -3F, 1, 3, 6);
        CaseSide2.setRotationPoint(0F, 6F, 0F);
        CaseSide2.mirror = true;
        setRotation(CaseSide2, 0F, 3.141593F, 0F);
        CaseBottom = new ModelRenderer(this, 16, 3);
        CaseBottom.addBox(-2F, 0F, -2F, 4, 1, 4);
        CaseBottom.setRotationPoint(0F, 14F, 0F);
        CaseBottom.mirror = true;
        setRotation(CaseBottom, 0F, 0F, 0F);
        CaseButtonBottom = new ModelRenderer(this, 20, 0);
        CaseButtonBottom.addBox(-1F, 0F, -1F, 2, 1, 2);
        CaseButtonBottom.setRotationPoint(0F, 15F, 0F);
        CaseButtonBottom.mirror = true;
        setRotation(CaseButtonBottom, 0F, 0F, 0F);
    }

    public void render (float start, float deltaTime)
    {
        /*super.render(entity, f, f1, f2, f3, f4, f5);*/
        setRotationAngles(start, deltaTime);
        Dynamo.render(start);
        CaseTop.render(start);
        CaseButtonTop.render(start);
        CaseSide1.render(start);
        CaseSide2.render(start);
        CaseBottom.render(start);
        CaseButtonBottom.render(start);
    }

    private void setRotationAngles (float start, float deltaTime)
    {
        Dynamo.rotateAngleY -= deltaTime / 16;
        CaseSide1.rotateAngleY += deltaTime / 8;
        CaseSide2.rotateAngleY += deltaTime / 8;
        CaseTop.rotateAngleY += deltaTime / 16;
        CaseBottom.rotateAngleY += deltaTime / 16;
    }

    private void setRotation (ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

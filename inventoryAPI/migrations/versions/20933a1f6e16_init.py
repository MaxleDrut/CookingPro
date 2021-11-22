"""init

Revision ID: 20933a1f6e16
Revises: 
Create Date: 2021-11-07 21:07:07.108280

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '20933a1f6e16'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('ingredient',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('kitchen', sa.String(length=50), nullable=True),
    sa.Column('hasUnit', sa.Boolean(), nullable=True),
    sa.Column('name', sa.String(length=50), nullable=True),
    sa.Column('unitName', sa.String(length=10), nullable=True),
    sa.Column('count', sa.Integer(), nullable=True),
    sa.Column('expires', sa.DateTime(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_ingredient_kitchen'), 'ingredient', ['kitchen'], unique=True)
    op.create_index(op.f('ix_ingredient_name'), 'ingredient', ['name'], unique=True)
    op.create_table('kitchen',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=50), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_kitchen_name'), 'kitchen', ['name'], unique=True)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index(op.f('ix_kitchen_name'), table_name='kitchen')
    op.drop_table('kitchen')
    op.drop_index(op.f('ix_ingredient_name'), table_name='ingredient')
    op.drop_index(op.f('ix_ingredient_kitchen'), table_name='ingredient')
    op.drop_table('ingredient')
    # ### end Alembic commands ###
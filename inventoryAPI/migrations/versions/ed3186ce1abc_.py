"""empty message

Revision ID: ed3186ce1abc
Revises: 77ff8144c66d
Create Date: 2021-11-08 19:11:48.840707

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'ed3186ce1abc'
down_revision = '77ff8144c66d'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index('ix_ingredient_name', table_name='ingredient')
    op.create_index(op.f('ix_ingredient_name'), 'ingredient', ['name'], unique=False)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index(op.f('ix_ingredient_name'), table_name='ingredient')
    op.create_index('ix_ingredient_name', 'ingredient', ['name'], unique=False)
    # ### end Alembic commands ###

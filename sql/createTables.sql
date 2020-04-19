CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username varchar(20) NOT NULL,
    password varchar(200) NOT NULL,
    hoards boolean[9] NOT NULL,
    gold int NOT NULL, 
    upgradeList boolean[10] NOT NULL
);
-- for now we have 10 universal upgrades
-- user starts off with 1 gold

CREATE TABLE hoard (
    hoard_id SERIAL PRIMARY KEY,
    user_id int4 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hoardType int NOT NULL,
    cost int NOT NULL,
    hoardLevel int NOT NULL,
    hoardItems real NOT NULL,
    productionSpeed real NOT NULL,
    goldConversionRate real NOT NULL,

);
-- cost is how much is costs to level up the hoard

CREATE TABLE hoardUpgrade (
    hoardUpgrade_id SERIAL PRIMARY KEY,
    hoard_id int4 NOT NULL REFERENCES hoard(id) ON DELETE CASCADE,
    description varchar(10000),
    cost int NOT NULL,
    unlocked boolean NOT NULL,
    additave real NOT NULL,
    multiplier real NOT NULL,
    goldMultiplier real NOT NULL
)
-- additave: for the initial upgrade that starts auto-generation. 
--      if the upgrade is for auto generation it will add its value to the initial 0 production speed of a hoard
--      otherwise it will be 0
-- multiplier: for any multipliers to production speed.
--      if the hoard does not influence upgrade speed, set to 0
-- goldMultiplier: multiplier for the gold conversion rate of a hoard
--      value of less than 1 usually
--      if upgrade does not influence gold conversion, set to 0
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username varchar(20) NOT NULL,
    password varchar(200) NOT NULL,
    gold int NOT NULL
);
CREATE TABLE univUpgrades (
    univUpgrade_id SERIAL PRIMARY KEY,
    user_id int4 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    upgradeType int NOT NULL,
    unlocked boolean NOT NULL
);
CREATE TABLE hoard (
    hoard_id SERIAL PRIMARY KEY,
    user_id int4 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hoardType int NOT NULL,
    cost int NOT NULL,
    hoardLevel int NOT NULL,
    hoardItems double precision NOT NULL,
    productionSpeed double precision NOT NULL,
    goldConversionRate double precision NOT NULL,
    unlocked boolean NOT NULL
);
CREATE TABLE hoardUpgrade (
    hoardUpgrade_id SERIAL PRIMARY KEY,
    hoard_id int4 NOT NULL REFERENCES hoard(hoard_id) ON DELETE CASCADE,
    upgradeNo int NOT NULL,
    cost int NOT NULL,
    unlocked boolean NOT NULL,
    newSpeed double precision NOT NULL,
    goldMultiplier double precision NOT NULL
);
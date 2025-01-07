import sqlite3
import random
from datetime import datetime

def generate_random_inventory(num_items):
    # List of possible units
    units = ['pieces', 'kg', 'g', 'L', 'mL', 'boxes']
    
    # List of adjectives and nouns to generate item names
    adjectives = ['Red', 'Blue', 'Green', 'Large', 'Small', 'Heavy', 'Light', 'Fresh', 'New', 'Old',
                 'Shiny', 'Dull', 'Smooth', 'Rough', 'Round', 'Square', 'Metal', 'Wooden', 'Plastic', 'Glass']
    
    nouns = ['Box', 'Container', 'Tool', 'Part', 'Device', 'Machine', 'Component', 'Material', 'Supply', 'Item',
             'Package', 'Kit', 'Set', 'Bundle', 'Pack', 'Unit', 'Piece', 'Block', 'Sheet', 'Roll']
    
    # Connect to database
    conn = sqlite3.connect('inventory.db')
    cursor = conn.cursor()
    
    # Create table if it doesn't exist
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS inventory
        (item_name TEXT PRIMARY KEY,
         quantity REAL,
         unit TEXT)
    ''')
    
    # Clear existing data
    cursor.execute('DELETE FROM inventory')
    
    # Generate random items
    items_added = 0
    attempts = 0
    used_names = set()
    
    print("\nGenerating inventory items...")
    
    while items_added < num_items and attempts < num_items * 2:
        # Generate random name
        name = f"{random.choice(adjectives)} {random.choice(nouns)}"
        
        # Skip if name already used
        if name in used_names:
            attempts += 1
            continue
            
        used_names.add(name)
        
        # Generate random quantity (between 1 and 100, with decimals for weight/volume)
        quantity = round(random.uniform(1, 100), 2)
        
        # Select random unit
        unit = random.choice(units)
        
        try:
            # Insert into database
            cursor.execute('''
                INSERT INTO inventory (item_name, quantity, unit)
                VALUES (?, ?, ?)
            ''', (name, quantity, unit))
            
            items_added += 1
            
            # Print progress
            if items_added % 10 == 0:
                print(f"Added {items_added} items...")
                
        except sqlite3.IntegrityError:
            attempts += 1
            continue
    
    # Commit changes and close connection
    conn.commit()
    
    # Print summary
    print("\nDatabase generation complete!")
    print(f"Successfully added {items_added} items")
    
    # Print sample of items
    print("\nSample of generated items:")
    cursor.execute('SELECT * FROM inventory LIMIT 5')
    for row in cursor.fetchall():
        print(f"Item: {row[0]:<25} Quantity: {row[1]:<8} Unit: {row[2]}")
    
    conn.close()

if __name__ == "__main__":
    while True:
        try:
            num_items = int(input("\nHow many inventory items would you like to generate? "))
            if num_items <= 0:
                print("Please enter a positive number")
                continue
            break
        except ValueError:
            print("Please enter a valid number")
    
    start_time = datetime.now()
    generate_random_inventory(num_items)
    end_time = datetime.now()
    
    print(f"\nTime taken: {end_time - start_time}")
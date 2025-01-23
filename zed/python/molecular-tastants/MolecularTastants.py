import pandas as pd
import numpy as np
from rdkit import Chem
from rdkit.Chem import AllChem, Descriptors
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
import xgboost as xgb
import lightgbm as lgb
from sklearn.metrics import balanced_accuracy_score
from rdkit import DataStructs

class MolecularTastePredictor:
    def __init__(self):
        self.model = None
        self.scaler = StandardScaler()
        
    def load_data(self, train_path, test_path=None):
        self.train_data = pd.read_csv(train_path)
        if test_path:
            self.test_data = pd.read_csv(test_path)
        return self
    
    def preprocess_smiles(self, smiles):
        mol = Chem.MolFromSmiles(smiles)
        if mol is None:
            return None
        return mol
    
    def generate_features(self, mol):
        if mol is None:
            return None
        
        features = {}
        
        # Calculate molecular descriptors
        features['MolWt'] = Descriptors.ExactMolWt(mol)
        features['LogP'] = Descriptors.MolLogP(mol)
        features['TPSA'] = Descriptors.TPSA(mol)
        features['HBA'] = Descriptors.NumHAcceptors(mol)
        features['HBD'] = Descriptors.NumHDonors(mol)
        features['Rotatable'] = Descriptors.NumRotatableBonds(mol)
        features['Aromatic'] = Descriptors.NumAromaticRings(mol)
        
        # Generate Morgan fingerprints using the newer method
        from rdkit.Chem.rdMolDescriptors import GetMorganGenerator
        mg = GetMorganGenerator(radius=2, fpSize=2048)
        fp = mg.GetFingerprint(mol)
        fp_bits = np.zeros((1,))
        DataStructs.ConvertToNumpyArray(fp, fp_bits)
        
        # Combine all features
        feature_vector = np.concatenate([
            np.array(list(features.values())),
            fp_bits
        ])
        
        return feature_vector
    
    def train_model(self):
        # Process SMILES and generate features
        X = []
        valid_indices = []
        
        for idx, row in self.train_data.iterrows():
            mol = self.preprocess_smiles(row['smiles'])
            if mol is not None:
                features = self.generate_features(mol)
                if features is not None:
                    X.append(features)
                    valid_indices.append(idx)
        
        X = np.array(X)
        y = self.train_data.iloc[valid_indices]['taste'].values
        
        # Scale features
        X_scaled = self.scaler.fit_transform(X)
        
        # Initialize models
        rf = RandomForestClassifier(n_estimators=100, random_state=42)
        xgb_model = xgb.XGBClassifier(random_state=42)
        lgb_model = lgb.LGBMClassifier(random_state=42)
        
        # Perform cross-validation and print scores
        models = [('Random Forest', rf), ('XGBoost', xgb_model), ('LightGBM', lgb_model)]
        for name, model in models:
            scores = cross_val_score(model, X_scaled, y, cv=5, scoring='balanced_accuracy')
            print(f"{name} CV Balanced Accuracy: {scores.mean():.4f} (+/- {scores.std() * 2:.4f})")
        
        # Train final models
        rf.fit(X_scaled, y)
        xgb_model.fit(X_scaled, y)
        lgb_model.fit(X_scaled, y)
        
        # Create ensemble
        self.models = [rf, xgb_model, lgb_model]
        return self
    
    def predict(self, smiles):
        mol = self.preprocess_smiles(smiles)
        if mol is None:
            return None
        
        features = self.generate_features(mol)
        if features is None:
            return None
        
        features_scaled = self.scaler.transform(features.reshape(1, -1))
        
        # Get predictions from all models
        predictions = []
        for model in self.models:
            pred = model.predict_proba(features_scaled)
            predictions.append(pred)
        
        # Average predictions
        final_pred = np.mean(predictions, axis=0)
        return np.argmax(final_pred)
    
    def create_submission(self, test_data, output_path):
        predictions = []
        molecule_ids = []
        
        for idx, row in test_data.iterrows():
            molecule_ids.append(row['molecule_id'])
            pred = self.predict(row['smiles'])
            predictions.append(pred if pred is not None else 0)
        
        submission_df = pd.DataFrame({
            'molecule_id': molecule_ids,
            'taste': predictions
        })
        
        submission_df.to_csv(output_path, index=False)
        return submission_df

def main():
    print("Starting molecular taste prediction...")
    predictor = MolecularTastePredictor()
    
    # Load and train
    print("Loading data...")
    predictor.load_data('train.csv', 'test.csv')
    
    print("Training models...")
    predictor.train_model()
    
    # Create submission
    print("Creating submission file...")
    submission_df = predictor.create_submission(predictor.test_data, 'submission.csv')
    
    print(f"Submission file created with {len(submission_df)} predictions")
    print("Done!")

if __name__ == "__main__":
    main()

"""
Export FaceNet (InceptionResnetV1) as a TorchScript model for DJL.

Usage:
    pip install facenet-pytorch torch
    python export_model.py

Output:
    models/face_feature.pt
"""

import os
import torch
from facenet_pytorch import InceptionResnetV1

def main():
    print("Loading InceptionResnetV1 pretrained on VGGFace2...")
    model = InceptionResnetV1(pretrained='vggface2').eval()

    # FaceNet expects 160x160 RGB input
    dummy_input = torch.randn(1, 3, 160, 160)

    print("Tracing model with torch.jit.trace...")
    traced_model = torch.jit.trace(model, dummy_input)

    output_dir = "models"
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, "face_feature.pt")

    traced_model.save(output_path)
    print(f"Model exported successfully to: {output_path}")

    # Verify the exported model
    print("Verifying exported model...")
    loaded = torch.jit.load(output_path)
    output = loaded(dummy_input)
    print(f"Output shape: {output.shape}")
    print(f"Embedding dimensions: {output.shape[1]}")
    print("Done!")

if __name__ == "__main__":
    main()
